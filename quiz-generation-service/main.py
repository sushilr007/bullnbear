from fastapi import FastAPI, HTTPException, Depends
from fastapi.middleware.cors import CORSMiddleware
from contextlib import asynccontextmanager
import httpx
import os
import json
from typing import List, Dict, Any
from loguru import logger
from sqlalchemy.orm import Session

# Import schemas and database
import sys
sys.path.append('../shared')
from models.schemas import *
from models.database import get_db, Quiz, Question, Answer, Book, Chapter

# Service URLs
RAG_SERVICE_URL = os.getenv("RAG_SERVICE_URL", "http://rag-service:8000")
LLM_SERVICE_URL = os.getenv("LLM_SERVICE_URL", "http://llm-service:8000")
CONTENT_SERVICE_URL = os.getenv("CONTENT_SERVICE_URL", "http://content-management:8000")

@asynccontextmanager
async def lifespan(app: FastAPI):
    # Startup
    logger.info("Starting Quiz Generation Service...")
    yield
    # Shutdown
    logger.info("Shutting down Quiz Generation Service...")

app = FastAPI(
    title="Quiz Generation Service",
    description="Orchestrates RAG and LLM approaches for quiz generation",
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

class QuizGenerationService:
    """Main service for orchestrating quiz generation"""
    
    def __init__(self):
        self.rag_service_url = RAG_SERVICE_URL
        self.llm_service_url = LLM_SERVICE_URL
        self.content_service_url = CONTENT_SERVICE_URL
    
    async def generate_rag_quiz(self, request: QuizGenerationRequest) -> Dict[str, Any]:
        """Generate quiz using RAG approach"""
        try:
            async with httpx.AsyncClient() as client:
                response = await client.post(
                    f"{self.rag_service_url}/quiz/generate",
                    json=request.model_dump(),
                    timeout=60.0
                )
                
                if response.status_code == 200:
                    return response.json()
                else:
                    raise HTTPException(status_code=response.status_code, detail=response.text)
                    
        except Exception as e:
            logger.error(f"RAG quiz generation error: {e}")
            raise HTTPException(status_code=500, detail=f"RAG generation failed: {str(e)}")
    
    async def generate_llm_quiz(self, request: QuizGenerationRequest) -> Dict[str, Any]:
        """Generate quiz using LLM approach"""
        try:
            # Convert to LLM request format
            llm_request = LLMGenerationRequest(
                topic=request.topic or f"Book {request.book_id}",
                subject=request.subject,
                difficulty=request.difficulty,
                num_questions=request.num_questions,
                question_types=request.question_types,
                context=request.context
            )
            
            async with httpx.AsyncClient() as client:
                response = await client.post(
                    f"{self.llm_service_url}/generate",
                    json=llm_request.model_dump(),
                    timeout=60.0
                )
                
                if response.status_code == 200:
                    return response.json()
                else:
                    raise HTTPException(status_code=response.status_code, detail=response.text)
                    
        except Exception as e:
            logger.error(f"LLM quiz generation error: {e}")
            raise HTTPException(status_code=500, detail=f"LLM generation failed: {str(e)}")
    
    async def generate_hybrid_quiz(self, request: QuizGenerationRequest) -> Dict[str, Any]:
        """Generate quiz using hybrid approach (RAG + LLM)"""
        try:
            # First, get context from RAG
            rag_context = await self._get_rag_context(request)
            
            # Then, use LLM with the retrieved context
            llm_request = LLMGenerationRequest(
                topic=request.topic or f"Book {request.book_id}",
                subject=request.subject,
                difficulty=request.difficulty,
                num_questions=request.num_questions,
                question_types=request.question_types,
                context=rag_context
            )
            
            async with httpx.AsyncClient() as client:
                response = await client.post(
                    f"{self.llm_service_url}/generate",
                    json=llm_request.model_dump(),
                    timeout=60.0
                )
                
                if response.status_code == 200:
                    result = response.json()
                    result["quiz_type"] = "hybrid"
                    result["rag_context_used"] = True
                    return result
                else:
                    raise HTTPException(status_code=response.status_code, detail=response.text)
                    
        except Exception as e:
            logger.error(f"Hybrid quiz generation error: {e}")
            raise HTTPException(status_code=500, detail=f"Hybrid generation failed: {str(e)}")
    
    async def _get_rag_context(self, request: QuizGenerationRequest) -> str:
        """Get context from RAG service"""
        try:
            # Build query based on request
            if request.book_id and request.chapter_id:
                query = f"Generate questions for chapter {request.chapter_id}"
            elif request.topic:
                query = f"Generate questions about {request.topic}"
            else:
                query = "Generate general questions"
            
            async with httpx.AsyncClient() as client:
                response = await client.post(
                    f"{self.rag_service_url}/documents/retrieve",
                    json={
                        "query": query,
                        "book_id": request.book_id,
                        "top_k": 10
                    },
                    timeout=30.0
                )
                
                if response.status_code == 200:
                    result = response.json()
                    # Combine chunks into context
                    chunks = result.get("chunks", [])
                    context = "\n\n".join([chunk["content"] for chunk in chunks])
                    return context
                else:
                    return ""
                    
        except Exception as e:
            logger.error(f"Error getting RAG context: {e}")
            return ""
    
    async def save_quiz_to_database(self, quiz_data: Dict[str, Any], user_id: int, db: Session) -> Quiz:
        """Save generated quiz to database"""
        try:
            # Create quiz record
            quiz = Quiz(
                title=quiz_data.get("title", "Generated Quiz"),
                description=quiz_data.get("description", ""),
                book_id=quiz_data.get("book_id"),
                chapter_id=quiz_data.get("chapter_id"),
                difficulty=quiz_data.get("difficulty", "medium"),
                quiz_type=quiz_data.get("quiz_type", "rag"),
                time_limit=quiz_data.get("time_limit"),
                total_points=0,
                created_by=user_id
            )
            
            db.add(quiz)
            db.flush()  # Get the quiz ID
            
            # Add questions
            questions_data = quiz_data.get("questions", [])
            total_points = 0
            
            for q_data in questions_data:
                question = Question(
                    quiz_id=quiz.id,
                    question_text=q_data["question_text"],
                    question_type=q_data["question_type"],
                    difficulty=q_data["difficulty"],
                    points=q_data["points"],
                    explanation=q_data.get("explanation", ""),
                    metadata=q_data.get("metadata", {})
                )
                
                db.add(question)
                db.flush()  # Get the question ID
                
                # Add answers
                for a_data in q_data.get("answers", []):
                    answer = Answer(
                        question_id=question.id,
                        answer_text=a_data["answer_text"],
                        is_correct=a_data["is_correct"],
                        explanation=a_data.get("explanation", ""),
                        order_index=a_data.get("order_index", 0)
                    )
                    db.add(answer)
                
                total_points += question.points
            
            # Update total points
            quiz.total_points = total_points
            
            db.commit()
            return quiz
            
        except Exception as e:
            db.rollback()
            logger.error(f"Error saving quiz to database: {e}")
            raise HTTPException(status_code=500, detail=f"Failed to save quiz: {str(e)}")

# Initialize service
quiz_service = QuizGenerationService()

# Health check
@app.get("/health")
async def health_check():
    """Health check endpoint"""
    return {"status": "healthy", "service": "quiz-generation-service"}

# Quiz generation endpoint
@app.post("/generate", response_model=QuizGenerationResponse)
async def generate_quiz(
    request: QuizGenerationRequest,
    user_id: int = 1,  # TODO: Get from authentication
    db: Session = Depends(get_db)
):
    """Generate quiz using specified approach"""
    try:
        # Choose generation method based on quiz_type
        if request.quiz_type == QuizType.RAG:
            quiz_data = await quiz_service.generate_rag_quiz(request)
        elif request.quiz_type == QuizType.LLM:
            quiz_data = await quiz_service.generate_llm_quiz(request)
        elif request.quiz_type == QuizType.HYBRID:
            quiz_data = await quiz_service.generate_hybrid_quiz(request)
        else:
            raise HTTPException(status_code=400, detail="Invalid quiz type")
        
        # Save to database
        quiz = await quiz_service.save_quiz_to_database(quiz_data, user_id, db)
        
        return QuizGenerationResponse(
            quiz_id=quiz.id,
            title=quiz.title,
            description=quiz.description,
            total_questions=len(quiz_data.get("questions", [])),
            estimated_time=len(quiz_data.get("questions", [])) * 2,  # 2 minutes per question
            status="generated"
        )
        
    except Exception as e:
        logger.error(f"Quiz generation error: {e}")
        raise HTTPException(status_code=500, detail=str(e))

# Get quizzes
@app.get("/quizzes", response_model=PaginatedResponse)
async def get_quizzes(
    page: int = 1,
    size: int = 10,
    book_id: int = None,
    quiz_type: QuizType = None,
    db: Session = Depends(get_db)
):
    """Get quizzes with pagination and filters"""
    try:
        query = db.query(Quiz)
        
        if book_id:
            query = query.filter(Quiz.book_id == book_id)
        
        if quiz_type:
            query = query.filter(Quiz.quiz_type == quiz_type.value)
        
        # Count total
        total = query.count()
        
        # Paginate
        offset = (page - 1) * size
        quizzes = query.offset(offset).limit(size).all()
        
        # Convert to response format
        items = []
        for quiz in quizzes:
            quiz_response = QuizResponse(
                id=quiz.id,
                title=quiz.title,
                description=quiz.description,
                book_id=quiz.book_id,
                chapter_id=quiz.chapter_id,
                difficulty=quiz.difficulty,
                quiz_type=quiz.quiz_type,
                time_limit=quiz.time_limit,
                total_points=quiz.total_points,
                created_by=quiz.created_by,
                created_at=quiz.created_at,
                updated_at=quiz.updated_at
            )
            items.append(quiz_response)
        
        return PaginatedResponse(
            items=items,
            total=total,
            page=page,
            size=size,
            pages=(total + size - 1) // size
        )
        
    except Exception as e:
        logger.error(f"Error getting quizzes: {e}")
        raise HTTPException(status_code=500, detail=str(e))

# Get quiz by ID
@app.get("/quizzes/{quiz_id}", response_model=QuizResponse)
async def get_quiz(quiz_id: int, db: Session = Depends(get_db)):
    """Get quiz by ID"""
    try:
        quiz = db.query(Quiz).filter(Quiz.id == quiz_id).first()
        if not quiz:
            raise HTTPException(status_code=404, detail="Quiz not found")
        
        return QuizResponse(
            id=quiz.id,
            title=quiz.title,
            description=quiz.description,
            book_id=quiz.book_id,
            chapter_id=quiz.chapter_id,
            difficulty=quiz.difficulty,
            quiz_type=quiz.quiz_type,
            time_limit=quiz.time_limit,
            total_points=quiz.total_points,
            created_by=quiz.created_by,
            created_at=quiz.created_at,
            updated_at=quiz.updated_at
        )
        
    except Exception as e:
        logger.error(f"Error getting quiz: {e}")
        raise HTTPException(status_code=500, detail=str(e))

# Get quiz questions
@app.get("/quizzes/{quiz_id}/questions")
async def get_quiz_questions(quiz_id: int, db: Session = Depends(get_db)):
    """Get questions for a specific quiz"""
    try:
        questions = db.query(Question).filter(Question.quiz_id == quiz_id).all()
        
        question_responses = []
        for question in questions:
            # Get answers for this question
            answers = db.query(Answer).filter(Answer.question_id == question.id).all()
            
            answer_responses = []
            for answer in answers:
                answer_response = AnswerResponse(
                    id=answer.id,
                    question_id=answer.question_id,
                    answer_text=answer.answer_text,
                    is_correct=answer.is_correct,
                    explanation=answer.explanation,
                    order_index=answer.order_index,
                    created_at=answer.created_at
                )
                answer_responses.append(answer_response)
            
            question_response = QuestionResponse(
                id=question.id,
                quiz_id=question.quiz_id,
                question_text=question.question_text,
                question_type=question.question_type,
                difficulty=question.difficulty,
                points=question.points,
                explanation=question.explanation,
                metadata=question.metadata,
                created_at=question.created_at
            )
            
            question_responses.append({
                "question": question_response,
                "answers": answer_responses
            })
        
        return {"questions": question_responses, "total": len(question_responses)}
        
    except Exception as e:
        logger.error(f"Error getting quiz questions: {e}")
        raise HTTPException(status_code=500, detail=str(e))

# Update quiz
@app.put("/quizzes/{quiz_id}", response_model=QuizResponse)
async def update_quiz(
    quiz_id: int,
    quiz_update: QuizUpdate,
    db: Session = Depends(get_db)
):
    """Update quiz"""
    try:
        quiz = db.query(Quiz).filter(Quiz.id == quiz_id).first()
        if not quiz:
            raise HTTPException(status_code=404, detail="Quiz not found")
        
        # Update fields
        update_data = quiz_update.model_dump(exclude_unset=True)
        for field, value in update_data.items():
            setattr(quiz, field, value)
        
        db.commit()
        db.refresh(quiz)
        
        return QuizResponse(
            id=quiz.id,
            title=quiz.title,
            description=quiz.description,
            book_id=quiz.book_id,
            chapter_id=quiz.chapter_id,
            difficulty=quiz.difficulty,
            quiz_type=quiz.quiz_type,
            time_limit=quiz.time_limit,
            total_points=quiz.total_points,
            created_by=quiz.created_by,
            created_at=quiz.created_at,
            updated_at=quiz.updated_at
        )
        
    except Exception as e:
        db.rollback()
        logger.error(f"Error updating quiz: {e}")
        raise HTTPException(status_code=500, detail=str(e))

# Delete quiz
@app.delete("/quizzes/{quiz_id}")
async def delete_quiz(quiz_id: int, db: Session = Depends(get_db)):
    """Delete quiz"""
    try:
        quiz = db.query(Quiz).filter(Quiz.id == quiz_id).first()
        if not quiz:
            raise HTTPException(status_code=404, detail="Quiz not found")
        
        db.delete(quiz)
        db.commit()
        
        return {"message": "Quiz deleted successfully"}
        
    except Exception as e:
        db.rollback()
        logger.error(f"Error deleting quiz: {e}")
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)