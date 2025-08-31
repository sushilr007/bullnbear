"""
LLM Service for Quiz Generation and Answer Validation
"""

import logging
import json
from typing import List, Dict, Any, Optional
from enum import Enum

from langchain.chat_models import ChatOpenAI
from langchain.schema import HumanMessage, SystemMessage
from langchain.prompts import PromptTemplate
from pydantic import BaseModel, Field

from app.core.config import settings

logger = logging.getLogger(__name__)


class QuestionType(str, Enum):
    """Question types supported by the quiz engine"""
    MULTIPLE_CHOICE = "multiple_choice"
    TRUE_FALSE = "true_false"
    FILL_IN_BLANK = "fill_in_blank"
    SHORT_ANSWER = "short_answer"
    ESSAY = "essay"


class DifficultyLevel(str, Enum):
    """Difficulty levels for questions"""
    EASY = "easy"
    MEDIUM = "medium"
    HARD = "hard"
    EXPERT = "expert"


class Question(BaseModel):
    """Question model"""
    question_text: str
    question_type: QuestionType
    difficulty: DifficultyLevel
    options: Optional[List[str]] = None
    correct_answer: str
    explanation: str
    hints: Optional[List[str]] = None
    points: int = 10
    topic: Optional[str] = None
    subtopic: Optional[str] = None


class LLMService:
    """
    Service for LLM-based quiz generation and processing
    """
    
    def __init__(self):
        """Initialize LLM service"""
        self.llm = self._initialize_llm()
        self.prompts = self._load_prompts()
    
    def _initialize_llm(self):
        """Initialize the LLM model"""
        if settings.OPENAI_API_KEY:
            return ChatOpenAI(
                openai_api_key=settings.OPENAI_API_KEY,
                model_name=settings.LLM_MODEL,
                temperature=settings.LLM_TEMPERATURE,
                max_tokens=2000
            )
        else:
            # Fallback to a local model or raise error
            raise ValueError("No LLM API key configured")
    
    def _load_prompts(self) -> Dict[str, PromptTemplate]:
        """Load prompt templates"""
        return {
            "generate_mcq": PromptTemplate(
                input_variables=["context", "topic", "difficulty", "num_questions"],
                template="""You are an expert educator creating multiple-choice questions.

Context: {context}
Topic: {topic}
Difficulty Level: {difficulty}
Number of Questions: {num_questions}

Generate {num_questions} multiple-choice questions based on the provided context.
Each question should:
1. Be clear and unambiguous
2. Have 4 options (A, B, C, D)
3. Have exactly one correct answer
4. Include an explanation for the correct answer
5. Be appropriate for the {difficulty} difficulty level

Return the questions in the following JSON format:
[
    {{
        "question_text": "Question text here",
        "options": ["Option A", "Option B", "Option C", "Option D"],
        "correct_answer": "A",
        "explanation": "Explanation of why this is correct",
        "topic": "{topic}"
    }}
]

Generate the questions now:"""
            ),
            
            "generate_true_false": PromptTemplate(
                input_variables=["context", "topic", "difficulty", "num_questions"],
                template="""You are an expert educator creating true/false questions.

Context: {context}
Topic: {topic}
Difficulty Level: {difficulty}
Number of Questions: {num_questions}

Generate {num_questions} true/false questions based on the provided context.
Each question should:
1. Be a clear statement that is either true or false
2. Test understanding of key concepts
3. Include an explanation
4. Be appropriate for the {difficulty} difficulty level

Return the questions in the following JSON format:
[
    {{
        "question_text": "Statement here",
        "correct_answer": "True" or "False",
        "explanation": "Explanation of why this is true/false",
        "topic": "{topic}"
    }}
]

Generate the questions now:"""
            ),
            
            "generate_fill_blank": PromptTemplate(
                input_variables=["context", "topic", "difficulty", "num_questions"],
                template="""You are an expert educator creating fill-in-the-blank questions.

Context: {context}
Topic: {topic}
Difficulty Level: {difficulty}
Number of Questions: {num_questions}

Generate {num_questions} fill-in-the-blank questions based on the provided context.
Each question should:
1. Have a clear blank indicated by _____ 
2. Test key terminology or concepts
3. Have a specific correct answer
4. Include an explanation
5. Be appropriate for the {difficulty} difficulty level

Return the questions in the following JSON format:
[
    {{
        "question_text": "The _____ is responsible for...",
        "correct_answer": "correct term",
        "explanation": "Explanation here",
        "hints": ["hint 1", "hint 2"],
        "topic": "{topic}"
    }}
]

Generate the questions now:"""
            ),
            
            "validate_answer": PromptTemplate(
                input_variables=["question", "user_answer", "correct_answer", "context"],
                template="""You are an expert educator evaluating a student's answer.

Question: {question}
Student's Answer: {user_answer}
Expected Answer: {correct_answer}
Context: {context}

Evaluate the student's answer and provide:
1. Whether it's correct, partially correct, or incorrect
2. A score from 0 to 100
3. Detailed feedback
4. Suggestions for improvement if needed

Return your evaluation in the following JSON format:
{{
    "is_correct": true/false,
    "score": 0-100,
    "feedback": "Detailed feedback here",
    "suggestions": ["suggestion 1", "suggestion 2"]
}}

Evaluate now:"""
            ),
            
            "generate_hints": PromptTemplate(
                input_variables=["question", "correct_answer", "difficulty"],
                template="""Generate helpful hints for this question without giving away the answer.

Question: {question}
Correct Answer: {correct_answer}
Difficulty: {difficulty}

Generate 3 progressive hints that guide the student toward the answer:
1. First hint: Very general guidance
2. Second hint: More specific direction
3. Third hint: Strong clue without revealing the answer

Return hints in JSON format:
{{
    "hints": ["hint 1", "hint 2", "hint 3"]
}}"""
            )
        }
    
    async def generate_questions(
        self,
        context: str,
        topic: str,
        question_type: QuestionType,
        difficulty: DifficultyLevel,
        num_questions: int = 5
    ) -> List[Question]:
        """
        Generate questions based on context and parameters
        
        Args:
            context: Educational content to generate questions from
            topic: Topic of the questions
            question_type: Type of questions to generate
            difficulty: Difficulty level
            num_questions: Number of questions to generate
        
        Returns:
            List of generated questions
        """
        try:
            # Select appropriate prompt
            if question_type == QuestionType.MULTIPLE_CHOICE:
                prompt = self.prompts["generate_mcq"]
            elif question_type == QuestionType.TRUE_FALSE:
                prompt = self.prompts["generate_true_false"]
            elif question_type == QuestionType.FILL_IN_BLANK:
                prompt = self.prompts["generate_fill_blank"]
            else:
                raise ValueError(f"Unsupported question type: {question_type}")
            
            # Format prompt
            formatted_prompt = prompt.format(
                context=context,
                topic=topic,
                difficulty=difficulty.value,
                num_questions=num_questions
            )
            
            # Generate questions
            response = await self.llm.apredict(formatted_prompt)
            
            # Parse response
            questions_data = self._parse_json_response(response)
            
            # Convert to Question objects
            questions = []
            for q_data in questions_data:
                question = Question(
                    question_text=q_data["question_text"],
                    question_type=question_type,
                    difficulty=difficulty,
                    options=q_data.get("options"),
                    correct_answer=q_data["correct_answer"],
                    explanation=q_data.get("explanation", ""),
                    hints=q_data.get("hints"),
                    topic=topic,
                    subtopic=q_data.get("subtopic")
                )
                questions.append(question)
            
            logger.info(f"Generated {len(questions)} {question_type} questions")
            return questions
            
        except Exception as e:
            logger.error(f"Error generating questions: {e}")
            return []
    
    async def generate_adaptive_questions(
        self,
        context: str,
        topic: str,
        user_level: str,
        previous_performance: Dict[str, Any]
    ) -> List[Question]:
        """
        Generate adaptive questions based on user performance
        
        Args:
            context: Educational content
            topic: Topic
            user_level: Current user level
            previous_performance: User's previous quiz performance
        
        Returns:
            Adaptive questions
        """
        # Determine difficulty based on performance
        if previous_performance.get("average_score", 0) > 80:
            difficulty = DifficultyLevel.HARD
        elif previous_performance.get("average_score", 0) > 60:
            difficulty = DifficultyLevel.MEDIUM
        else:
            difficulty = DifficultyLevel.EASY
        
        # Mix question types
        questions = []
        
        # Generate different types of questions
        mcq_questions = await self.generate_questions(
            context, topic, QuestionType.MULTIPLE_CHOICE, difficulty, 3
        )
        tf_questions = await self.generate_questions(
            context, topic, QuestionType.TRUE_FALSE, difficulty, 2
        )
        
        questions.extend(mcq_questions)
        questions.extend(tf_questions)
        
        return questions
    
    async def validate_answer(
        self,
        question: str,
        user_answer: str,
        correct_answer: str,
        context: Optional[str] = None
    ) -> Dict[str, Any]:
        """
        Validate a user's answer using LLM
        
        Args:
            question: The question text
            user_answer: User's answer
            correct_answer: Correct answer
            context: Additional context
        
        Returns:
            Validation result with score and feedback
        """
        try:
            prompt = self.prompts["validate_answer"]
            formatted_prompt = prompt.format(
                question=question,
                user_answer=user_answer,
                correct_answer=correct_answer,
                context=context or "No additional context"
            )
            
            response = await self.llm.apredict(formatted_prompt)
            result = self._parse_json_response(response)
            
            return result
            
        except Exception as e:
            logger.error(f"Error validating answer: {e}")
            # Fallback to simple comparison
            is_correct = user_answer.lower().strip() == correct_answer.lower().strip()
            return {
                "is_correct": is_correct,
                "score": 100 if is_correct else 0,
                "feedback": "Answer validated using simple comparison",
                "suggestions": []
            }
    
    async def generate_hints(
        self,
        question: str,
        correct_answer: str,
        difficulty: DifficultyLevel
    ) -> List[str]:
        """
        Generate hints for a question
        
        Args:
            question: Question text
            correct_answer: Correct answer
            difficulty: Difficulty level
        
        Returns:
            List of hints
        """
        try:
            prompt = self.prompts["generate_hints"]
            formatted_prompt = prompt.format(
                question=question,
                correct_answer=correct_answer,
                difficulty=difficulty.value
            )
            
            response = await self.llm.apredict(formatted_prompt)
            result = self._parse_json_response(response)
            
            return result.get("hints", [])
            
        except Exception as e:
            logger.error(f"Error generating hints: {e}")
            return ["Think about the key concepts", "Review the material again"]
    
    async def generate_explanation(
        self,
        question: str,
        correct_answer: str,
        context: str
    ) -> str:
        """
        Generate detailed explanation for an answer
        
        Args:
            question: Question text
            correct_answer: Correct answer
            context: Related context
        
        Returns:
            Detailed explanation
        """
        try:
            prompt = f"""Generate a clear, educational explanation for this question and answer.

Question: {question}
Correct Answer: {correct_answer}
Context: {context}

Provide a detailed explanation that helps the student understand:
1. Why this answer is correct
2. The underlying concepts
3. Common misconceptions to avoid

Explanation:"""
            
            response = await self.llm.apredict(prompt)
            return response.strip()
            
        except Exception as e:
            logger.error(f"Error generating explanation: {e}")
            return "Explanation generation failed."
    
    async def summarize_content(
        self,
        content: str,
        max_length: int = 500
    ) -> str:
        """
        Summarize educational content
        
        Args:
            content: Content to summarize
            max_length: Maximum summary length
        
        Returns:
            Summary
        """
        try:
            prompt = f"""Summarize the following educational content in {max_length} characters or less.
Focus on key concepts and important points.

Content: {content}

Summary:"""
            
            response = await self.llm.apredict(prompt)
            return response.strip()
            
        except Exception as e:
            logger.error(f"Error summarizing content: {e}")
            return content[:max_length] + "..."
    
    def _parse_json_response(self, response: str) -> Any:
        """Parse JSON response from LLM"""
        try:
            # Try to extract JSON from the response
            start_idx = response.find('[') if '[' in response else response.find('{')
            end_idx = response.rfind(']') + 1 if ']' in response else response.rfind('}') + 1
            
            if start_idx != -1 and end_idx != 0:
                json_str = response[start_idx:end_idx]
                return json.loads(json_str)
            else:
                return json.loads(response)
                
        except json.JSONDecodeError as e:
            logger.error(f"Failed to parse JSON response: {e}")
            logger.debug(f"Response: {response}")
            return [] if '[' in response else {}