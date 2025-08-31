from fastapi import FastAPI, HTTPException, Depends
from fastapi.middleware.cors import CORSMiddleware
from contextlib import asynccontextmanager
import httpx
import os
import json
from typing import List, Dict, Any
from loguru import logger
import openai
from anthropic import Anthropic
import asyncio

# Import schemas
import sys
sys.path.append('../shared')
from models.schemas import *

# LLM API Keys
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
ANTHROPIC_API_KEY = os.getenv("ANTHROPIC_API_KEY")

# Initialize LLM clients
if OPENAI_API_KEY:
    openai_client = openai.AsyncOpenAI(api_key=OPENAI_API_KEY)
else:
    openai_client = None

if ANTHROPIC_API_KEY:
    anthropic_client = Anthropic(api_key=ANTHROPIC_API_KEY)
else:
    anthropic_client = None

@asynccontextmanager
async def lifespan(app: FastAPI):
    # Startup
    logger.info("Starting LLM Service...")
    if not openai_client and not anthropic_client:
        logger.warning("No LLM API keys configured")
    yield
    # Shutdown
    logger.info("Shutting down LLM Service...")

app = FastAPI(
    title="LLM Service",
    description="Direct LLM-based quiz generation",
    version="1.0.0",
    lifespan=lifespan
)

# CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

class LLMService:
    """Main LLM service for quiz generation"""
    
    def __init__(self):
        self.openai_client = openai_client
        self.anthropic_client = anthropic_client
    
    async def generate_questions_openai(self, prompt: str, num_questions: int = 5) -> List[Dict[str, Any]]:
        """Generate questions using OpenAI"""
        if not self.openai_client:
            raise HTTPException(status_code=500, detail="OpenAI client not configured")
        
        try:
            response = await self.openai_client.chat.completions.create(
                model="gpt-4",
                messages=[
                    {
                        "role": "system",
                        "content": "You are an expert quiz generator. Generate high-quality educational questions based on the given topic. Return the response as a JSON array of question objects."
                    },
                    {
                        "role": "user",
                        "content": prompt
                    }
                ],
                temperature=0.7,
                max_tokens=2000
            )
            
            # Parse response
            content = response.choices[0].message.content
            try:
                questions = json.loads(content)
                return questions
            except json.JSONDecodeError:
                # Fallback: parse manually if JSON parsing fails
                return self._parse_questions_manually(content)
                
        except Exception as e:
            logger.error(f"OpenAI API error: {e}")
            raise HTTPException(status_code=500, detail=f"OpenAI API error: {str(e)}")
    
    async def generate_questions_anthropic(self, prompt: str, num_questions: int = 5) -> List[Dict[str, Any]]:
        """Generate questions using Anthropic Claude"""
        if not self.anthropic_client:
            raise HTTPException(status_code=500, detail="Anthropic client not configured")
        
        try:
            response = await asyncio.to_thread(
                self.anthropic_client.messages.create,
                model="claude-3-sonnet-20240229",
                max_tokens=2000,
                messages=[
                    {
                        "role": "user",
                        "content": prompt
                    }
                ]
            )
            
            content = response.content[0].text
            try:
                questions = json.loads(content)
                return questions
            except json.JSONDecodeError:
                return self._parse_questions_manually(content)
                
        except Exception as e:
            logger.error(f"Anthropic API error: {e}")
            raise HTTPException(status_code=500, detail=f"Anthropic API error: {str(e)}")
    
    def _parse_questions_manually(self, content: str) -> List[Dict[str, Any]]:
        """Parse questions manually if JSON parsing fails"""
        # This is a fallback method
        questions = []
        lines = content.split('\n')
        current_question = None
        
        for line in lines:
            line = line.strip()
            if line.startswith('Q:') or line.startswith('Question:'):
                if current_question:
                    questions.append(current_question)
                current_question = {
                    "question_text": line.split(':', 1)[1].strip(),
                    "question_type": "mcq",
                    "difficulty": "medium",
                    "points": 1,
                    "answers": []
                }
            elif line.startswith('A:') or line.startswith('Answer:'):
                if current_question:
                    current_question["answers"].append({
                        "answer_text": line.split(':', 1)[1].strip(),
                        "is_correct": True
                    })
        
        if current_question:
            questions.append(current_question)
        
        return questions
    
    def _build_prompt(self, topic: str, subject: str = None, difficulty: str = "medium", 
                     num_questions: int = 5, question_types: List[str] = ["mcq"], 
                     context: str = None) -> str:
        """Build a comprehensive prompt for question generation"""
        
        prompt = f"""Generate {num_questions} high-quality educational questions about "{topic}"."""
        
        if subject:
            prompt += f" The subject is {subject}."
        
        prompt += f" Difficulty level: {difficulty}."
        
        # Add question type instructions
        type_instructions = []
        for q_type in question_types:
            if q_type == "mcq":
                type_instructions.append("multiple choice questions with 4 options (A, B, C, D)")
            elif q_type == "true_false":
                type_instructions.append("true/false questions")
            elif q_type == "fill_blank":
                type_instructions.append("fill-in-the-blank questions")
            elif q_type == "essay":
                type_instructions.append("essay questions")
        
        prompt += f" Question types: {', '.join(type_instructions)}."
        
        if context:
            prompt += f"\n\nContext: {context}"
        
        prompt += """

Please return the response as a JSON array with the following structure:
[
  {
    "question_text": "The question text",
    "question_type": "mcq|true_false|fill_blank|essay",
    "difficulty": "easy|medium|hard",
    "points": 1,
    "explanation": "Explanation of the correct answer",
    "answers": [
      {
        "answer_text": "Answer option text",
        "is_correct": true/false,
        "explanation": "Why this answer is correct/incorrect"
      }
    ]
  }
]

For multiple choice questions, include exactly 4 answer options with only one correct answer.
For true/false questions, include 2 answer options.
For fill-in-the-blank questions, provide the correct answer.
For essay questions, provide a sample answer or key points."""
        
        return prompt
    
    async def generate_quiz(self, request: LLMGenerationRequest) -> Dict[str, Any]:
        """Generate a complete quiz using LLM"""
        try:
            # Build prompt
            prompt = self._build_prompt(
                topic=request.topic,
                subject=request.subject,
                difficulty=request.difficulty.value,
                num_questions=request.num_questions,
                question_types=[qt.value for qt in request.question_types],
                context=request.context
            )
            
            # Choose LLM provider (prefer OpenAI if available)
            if self.openai_client:
                questions = await self.generate_questions_openai(prompt, request.num_questions)
            elif self.anthropic_client:
                questions = await self.generate_questions_anthropic(prompt, request.num_questions)
            else:
                raise HTTPException(status_code=500, detail="No LLM provider configured")
            
            # Validate and format questions
            validated_questions = self._validate_questions(questions)
            
            return {
                "quiz_type": "llm",
                "topic": request.topic,
                "subject": request.subject,
                "difficulty": request.difficulty.value,
                "questions": validated_questions,
                "total_questions": len(validated_questions)
            }
            
        except Exception as e:
            logger.error(f"Quiz generation error: {e}")
            raise HTTPException(status_code=500, detail=str(e))
    
    def _validate_questions(self, questions: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
        """Validate and format questions"""
        validated = []
        
        for i, question in enumerate(questions):
            # Ensure required fields
            if "question_text" not in question:
                continue
            
            # Set defaults
            validated_question = {
                "question_text": question["question_text"],
                "question_type": question.get("question_type", "mcq"),
                "difficulty": question.get("difficulty", "medium"),
                "points": question.get("points", 1),
                "explanation": question.get("explanation", ""),
                "answers": question.get("answers", [])
            }
            
            # Validate answers
            if validated_question["question_type"] == "mcq":
                if len(validated_question["answers"]) != 4:
                    # Generate default answers if invalid
                    validated_question["answers"] = [
                        {"answer_text": "Option A", "is_correct": True},
                        {"answer_text": "Option B", "is_correct": False},
                        {"answer_text": "Option C", "is_correct": False},
                        {"answer_text": "Option D", "is_correct": False}
                    ]
            elif validated_question["question_type"] == "true_false":
                if len(validated_question["answers"]) != 2:
                    validated_question["answers"] = [
                        {"answer_text": "True", "is_correct": True},
                        {"answer_text": "False", "is_correct": False}
                    ]
            
            # Ensure at least one correct answer
            correct_answers = [a for a in validated_question["answers"] if a.get("is_correct", False)]
            if not correct_answers and validated_question["answers"]:
                validated_question["answers"][0]["is_correct"] = True
            
            validated.append(validated_question)
        
        return validated

# Initialize LLM service
llm_service = LLMService()

# Health check
@app.get("/health")
async def health_check():
    """Health check endpoint"""
    return {
        "status": "healthy", 
        "service": "llm-service",
        "openai_configured": openai_client is not None,
        "anthropic_configured": anthropic_client is not None
    }

# Question generation
@app.post("/questions/generate")
async def generate_questions(
    topic: str,
    subject: str = None,
    difficulty: DifficultyLevel = DifficultyLevel.MEDIUM,
    num_questions: int = 5,
    question_types: List[QuestionType] = [QuestionType.MCQ],
    context: str = None
):
    """Generate questions using LLM"""
    try:
        request = LLMGenerationRequest(
            topic=topic,
            subject=subject,
            difficulty=difficulty,
            num_questions=num_questions,
            question_types=question_types,
            context=context
        )
        
        result = await llm_service.generate_quiz(request)
        return result
    except Exception as e:
        logger.error(f"Question generation error: {e}")
        raise HTTPException(status_code=500, detail=str(e))

# Quiz generation
@app.post("/generate", response_model=QuizGenerationResponse)
async def generate_quiz(request: LLMGenerationRequest):
    """Generate complete quiz using LLM"""
    try:
        result = await llm_service.generate_quiz(request)
        
        # Convert to QuizGenerationResponse format
        return QuizGenerationResponse(
            quiz_id=0,  # Will be set by quiz generation service
            title=f"LLM Quiz: {request.topic}",
            description=f"Generated quiz about {request.topic}",
            total_questions=result["total_questions"],
            estimated_time=result["total_questions"] * 2,  # 2 minutes per question
            status="generated"
        )
    except Exception as e:
        logger.error(f"Quiz generation error: {e}")
        raise HTTPException(status_code=500, detail=str(e))

# Provider-specific endpoints
@app.post("/openai/generate")
async def generate_openai_questions(
    topic: str,
    num_questions: int = 5
):
    """Generate questions using OpenAI specifically"""
    try:
        prompt = llm_service._build_prompt(topic, num_questions=num_questions)
        questions = await llm_service.generate_questions_openai(prompt, num_questions)
        return {"questions": questions, "provider": "openai"}
    except Exception as e:
        logger.error(f"OpenAI generation error: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/anthropic/generate")
async def generate_anthropic_questions(
    topic: str,
    num_questions: int = 5
):
    """Generate questions using Anthropic specifically"""
    try:
        prompt = llm_service._build_prompt(topic, num_questions=num_questions)
        questions = await llm_service.generate_questions_anthropic(prompt, num_questions)
        return {"questions": questions, "provider": "anthropic"}
    except Exception as e:
        logger.error(f"Anthropic generation error: {e}")
        raise HTTPException(status_code=500, detail=str(e))

# Prompt testing
@app.post("/test-prompt")
async def test_prompt(request: LLMGenerationRequest):
    """Test prompt generation without calling LLM"""
    try:
        prompt = llm_service._build_prompt(
            topic=request.topic,
            subject=request.subject,
            difficulty=request.difficulty.value,
            num_questions=request.num_questions,
            question_types=[qt.value for qt in request.question_types],
            context=request.context
        )
        return {"prompt": prompt, "length": len(prompt)}
    except Exception as e:
        logger.error(f"Prompt test error: {e}")
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)