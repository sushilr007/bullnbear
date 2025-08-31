"""
Quiz Engine Service - Core orchestration for quiz generation and management
"""

import logging
import uuid
from datetime import datetime, timedelta
from typing import List, Dict, Any, Optional
from enum import Enum

from app.services.rag_service import RAGService
from app.services.llm_service import LLMService, Question, QuestionType, DifficultyLevel
from app.core.config import settings

logger = logging.getLogger(__name__)


class QuizMode(str, Enum):
    """Quiz generation modes"""
    RAG_BASED = "rag_based"  # Generate from uploaded content
    LLM_BASED = "llm_based"  # Generate from LLM knowledge
    HYBRID = "hybrid"  # Combine RAG and LLM
    QUESTION_BANK = "question_bank"  # Use existing questions


class QuizStatus(str, Enum):
    """Quiz status"""
    DRAFT = "draft"
    ACTIVE = "active"
    COMPLETED = "completed"
    EXPIRED = "expired"


class Quiz(BaseModel):
    """Quiz model"""
    quiz_id: str
    title: str
    description: Optional[str]
    mode: QuizMode
    questions: List[Question]
    time_limit: Optional[int]  # in minutes
    created_at: datetime
    status: QuizStatus
    metadata: Dict[str, Any]


class QuizEngine:
    """
    Main quiz engine orchestrating RAG and LLM services
    """
    
    def __init__(self):
        """Initialize quiz engine with services"""
        self.rag_service = RAGService()
        self.llm_service = LLMService()
        self.active_quizzes = {}  # In-memory storage for demo
    
    async def generate_quiz(
        self,
        mode: QuizMode,
        topic: str,
        parameters: Dict[str, Any]
    ) -> Quiz:
        """
        Generate a quiz based on mode and parameters
        
        Args:
            mode: Quiz generation mode
            topic: Quiz topic
            parameters: Additional parameters (difficulty, num_questions, etc.)
        
        Returns:
            Generated quiz
        """
        try:
            quiz_id = str(uuid.uuid4())
            questions = []
            
            if mode == QuizMode.RAG_BASED:
                questions = await self._generate_rag_quiz(topic, parameters)
            elif mode == QuizMode.LLM_BASED:
                questions = await self._generate_llm_quiz(topic, parameters)
            elif mode == QuizMode.HYBRID:
                questions = await self._generate_hybrid_quiz(topic, parameters)
            elif mode == QuizMode.QUESTION_BANK:
                questions = await self._get_questions_from_bank(topic, parameters)
            
            # Create quiz object
            quiz = Quiz(
                quiz_id=quiz_id,
                title=parameters.get("title", f"Quiz on {topic}"),
                description=parameters.get("description"),
                mode=mode,
                questions=questions,
                time_limit=parameters.get("time_limit", settings.QUIZ_TIME_LIMIT_MINUTES),
                created_at=datetime.utcnow(),
                status=QuizStatus.ACTIVE,
                metadata={
                    "topic": topic,
                    "difficulty": parameters.get("difficulty", "medium"),
                    "grade_level": parameters.get("grade_level"),
                    "subject": parameters.get("subject")
                }
            )
            
            # Store quiz
            self.active_quizzes[quiz_id] = quiz
            
            logger.info(f"Generated quiz {quiz_id} with {len(questions)} questions")
            return quiz
            
        except Exception as e:
            logger.error(f"Error generating quiz: {e}")
            raise
    
    async def _generate_rag_quiz(
        self,
        topic: str,
        parameters: Dict[str, Any]
    ) -> List[Question]:
        """Generate quiz using RAG from uploaded content"""
        
        # Retrieve relevant context
        context_results = await self.rag_service.search_by_topic(
            topic=topic,
            subject=parameters.get("subject"),
            grade_level=parameters.get("grade_level"),
            top_k=10
        )
        
        if not context_results:
            logger.warning(f"No context found for topic: {topic}")
            return []
        
        # Combine context
        combined_context = "\n\n".join([
            result["content"] for result in context_results[:5]
        ])
        
        # Generate questions from context
        questions = []
        
        # Determine question distribution
        num_questions = parameters.get("num_questions", settings.DEFAULT_QUESTIONS_PER_QUIZ)
        difficulty = DifficultyLevel(parameters.get("difficulty", "medium"))
        
        # Generate different types of questions
        if num_questions >= 5:
            mcq_count = num_questions // 2
            tf_count = num_questions // 4
            fill_count = num_questions - mcq_count - tf_count
        else:
            mcq_count = num_questions
            tf_count = 0
            fill_count = 0
        
        # Generate MCQ questions
        if mcq_count > 0:
            mcq_questions = await self.llm_service.generate_questions(
                context=combined_context,
                topic=topic,
                question_type=QuestionType.MULTIPLE_CHOICE,
                difficulty=difficulty,
                num_questions=mcq_count
            )
            questions.extend(mcq_questions)
        
        # Generate True/False questions
        if tf_count > 0:
            tf_questions = await self.llm_service.generate_questions(
                context=combined_context,
                topic=topic,
                question_type=QuestionType.TRUE_FALSE,
                difficulty=difficulty,
                num_questions=tf_count
            )
            questions.extend(tf_questions)
        
        # Generate Fill-in-the-blank questions
        if fill_count > 0:
            fill_questions = await self.llm_service.generate_questions(
                context=combined_context,
                topic=topic,
                question_type=QuestionType.FILL_IN_BLANK,
                difficulty=difficulty,
                num_questions=fill_count
            )
            questions.extend(fill_questions)
        
        return questions
    
    async def _generate_llm_quiz(
        self,
        topic: str,
        parameters: Dict[str, Any]
    ) -> List[Question]:
        """Generate quiz using LLM knowledge"""
        
        # Create a general context prompt
        context = f"""Generate educational quiz questions about {topic}.
        Subject: {parameters.get('subject', 'General')}
        Grade Level: {parameters.get('grade_level', 'High School')}
        Focus on key concepts, important facts, and understanding."""
        
        num_questions = parameters.get("num_questions", settings.DEFAULT_QUESTIONS_PER_QUIZ)
        difficulty = DifficultyLevel(parameters.get("difficulty", "medium"))
        
        # Generate questions
        questions = await self.llm_service.generate_questions(
            context=context,
            topic=topic,
            question_type=QuestionType.MULTIPLE_CHOICE,
            difficulty=difficulty,
            num_questions=num_questions
        )
        
        return questions
    
    async def _generate_hybrid_quiz(
        self,
        topic: str,
        parameters: Dict[str, Any]
    ) -> List[Question]:
        """Generate quiz using both RAG and LLM"""
        
        num_questions = parameters.get("num_questions", settings.DEFAULT_QUESTIONS_PER_QUIZ)
        
        # Split between RAG and LLM
        rag_count = num_questions // 2
        llm_count = num_questions - rag_count
        
        # Generate RAG-based questions
        parameters_rag = parameters.copy()
        parameters_rag["num_questions"] = rag_count
        rag_questions = await self._generate_rag_quiz(topic, parameters_rag)
        
        # Generate LLM-based questions
        parameters_llm = parameters.copy()
        parameters_llm["num_questions"] = llm_count
        llm_questions = await self._generate_llm_quiz(topic, parameters_llm)
        
        # Combine and shuffle
        all_questions = rag_questions + llm_questions
        import random
        random.shuffle(all_questions)
        
        return all_questions
    
    async def _get_questions_from_bank(
        self,
        topic: str,
        parameters: Dict[str, Any]
    ) -> List[Question]:
        """Get questions from existing question bank"""
        # This would query a database of existing questions
        # For now, return empty list
        logger.info("Question bank not yet implemented")
        return []
    
    async def evaluate_quiz(
        self,
        quiz_id: str,
        answers: List[Dict[str, Any]]
    ) -> Dict[str, Any]:
        """
        Evaluate quiz answers
        
        Args:
            quiz_id: Quiz ID
            answers: List of user answers
        
        Returns:
            Evaluation results
        """
        try:
            quiz = self.active_quizzes.get(quiz_id)
            if not quiz:
                raise ValueError(f"Quiz {quiz_id} not found")
            
            results = {
                "quiz_id": quiz_id,
                "total_questions": len(quiz.questions),
                "answers_evaluated": [],
                "total_score": 0,
                "max_score": 0,
                "percentage": 0,
                "feedback": []
            }
            
            for i, answer in enumerate(answers):
                if i >= len(quiz.questions):
                    break
                
                question = quiz.questions[i]
                user_answer = answer.get("answer", "")
                
                # Validate answer
                if question.question_type in [QuestionType.SHORT_ANSWER, QuestionType.ESSAY]:
                    # Use LLM for validation
                    validation = await self.llm_service.validate_answer(
                        question=question.question_text,
                        user_answer=user_answer,
                        correct_answer=question.correct_answer
                    )
                    is_correct = validation["is_correct"]
                    score = validation["score"]
                    feedback = validation["feedback"]
                else:
                    # Simple comparison for objective questions
                    is_correct = user_answer.lower().strip() == question.correct_answer.lower().strip()
                    score = 100 if is_correct else 0
                    feedback = question.explanation
                
                answer_result = {
                    "question_index": i,
                    "question": question.question_text,
                    "user_answer": user_answer,
                    "correct_answer": question.correct_answer,
                    "is_correct": is_correct,
                    "score": score,
                    "feedback": feedback,
                    "explanation": question.explanation
                }
                
                results["answers_evaluated"].append(answer_result)
                results["total_score"] += score * question.points / 100
                results["max_score"] += question.points
            
            # Calculate percentage
            if results["max_score"] > 0:
                results["percentage"] = (results["total_score"] / results["max_score"]) * 100
            
            # Update quiz status
            quiz.status = QuizStatus.COMPLETED
            
            return results
            
        except Exception as e:
            logger.error(f"Error evaluating quiz: {e}")
            raise
    
    async def get_quiz(self, quiz_id: str) -> Optional[Quiz]:
        """Get quiz by ID"""
        return self.active_quizzes.get(quiz_id)
    
    async def get_hint(
        self,
        quiz_id: str,
        question_index: int
    ) -> List[str]:
        """
        Get hints for a question
        
        Args:
            quiz_id: Quiz ID
            question_index: Question index
        
        Returns:
            List of hints
        """
        quiz = self.active_quizzes.get(quiz_id)
        if not quiz or question_index >= len(quiz.questions):
            return []
        
        question = quiz.questions[question_index]
        
        if question.hints:
            return question.hints
        
        # Generate hints if not available
        hints = await self.llm_service.generate_hints(
            question=question.question_text,
            correct_answer=question.correct_answer,
            difficulty=question.difficulty
        )
        
        # Cache hints
        question.hints = hints
        
        return hints
    
    async def get_quiz_analytics(self, quiz_id: str) -> Dict[str, Any]:
        """
        Get analytics for a quiz
        
        Args:
            quiz_id: Quiz ID
        
        Returns:
            Quiz analytics
        """
        quiz = self.active_quizzes.get(quiz_id)
        if not quiz:
            return {}
        
        analytics = {
            "quiz_id": quiz_id,
            "title": quiz.title,
            "total_questions": len(quiz.questions),
            "question_types": {},
            "difficulty_distribution": {},
            "topics": [],
            "created_at": quiz.created_at.isoformat(),
            "status": quiz.status.value
        }
        
        # Analyze questions
        for question in quiz.questions:
            # Question type distribution
            q_type = question.question_type.value
            analytics["question_types"][q_type] = analytics["question_types"].get(q_type, 0) + 1
            
            # Difficulty distribution
            difficulty = question.difficulty.value
            analytics["difficulty_distribution"][difficulty] = \
                analytics["difficulty_distribution"].get(difficulty, 0) + 1
            
            # Topics
            if question.topic and question.topic not in analytics["topics"]:
                analytics["topics"].append(question.topic)
        
        return analytics