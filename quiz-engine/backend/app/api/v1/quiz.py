"""
Quiz API endpoints
"""

from fastapi import APIRouter, HTTPException, Depends, Query
from typing import List, Dict, Any, Optional
from pydantic import BaseModel

from app.services.quiz_engine import QuizEngine, QuizMode, QuizStatus
from app.services.llm_service import QuestionType, DifficultyLevel

router = APIRouter()

# Initialize quiz engine
quiz_engine = QuizEngine()


class QuizGenerationRequest(BaseModel):
    """Quiz generation request model"""
    mode: QuizMode
    topic: str
    title: Optional[str] = None
    description: Optional[str] = None
    num_questions: int = 10
    difficulty: str = "medium"
    question_types: Optional[List[QuestionType]] = None
    time_limit: Optional[int] = 30
    subject: Optional[str] = None
    grade_level: Optional[str] = None
    tags: Optional[List[str]] = None


class QuizSubmission(BaseModel):
    """Quiz submission model"""
    quiz_id: str
    answers: List[Dict[str, Any]]
    time_taken: Optional[int] = None


class QuizResponse(BaseModel):
    """Quiz response model"""
    quiz_id: str
    title: str
    description: Optional[str]
    mode: str
    total_questions: int
    time_limit: Optional[int]
    questions: List[Dict[str, Any]]
    metadata: Dict[str, Any]


@router.post("/generate", response_model=QuizResponse)
async def generate_quiz(request: QuizGenerationRequest):
    """
    Generate a new quiz based on specified parameters
    """
    try:
        parameters = {
            "title": request.title,
            "description": request.description,
            "num_questions": request.num_questions,
            "difficulty": request.difficulty,
            "question_types": request.question_types,
            "time_limit": request.time_limit,
            "subject": request.subject,
            "grade_level": request.grade_level,
            "tags": request.tags
        }
        
        quiz = await quiz_engine.generate_quiz(
            mode=request.mode,
            topic=request.topic,
            parameters=parameters
        )
        
        # Format questions for response
        formatted_questions = []
        for i, question in enumerate(quiz.questions):
            formatted_questions.append({
                "index": i,
                "question_text": question.question_text,
                "question_type": question.question_type.value,
                "options": question.options,
                "points": question.points,
                "difficulty": question.difficulty.value,
                "has_hints": bool(question.hints)
            })
        
        return QuizResponse(
            quiz_id=quiz.quiz_id,
            title=quiz.title,
            description=quiz.description,
            mode=quiz.mode.value,
            total_questions=len(quiz.questions),
            time_limit=quiz.time_limit,
            questions=formatted_questions,
            metadata=quiz.metadata
        )
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.get("/{quiz_id}", response_model=QuizResponse)
async def get_quiz(quiz_id: str):
    """
    Get quiz by ID
    """
    quiz = await quiz_engine.get_quiz(quiz_id)
    if not quiz:
        raise HTTPException(status_code=404, detail="Quiz not found")
    
    # Format questions for response
    formatted_questions = []
    for i, question in enumerate(quiz.questions):
        formatted_questions.append({
            "index": i,
            "question_text": question.question_text,
            "question_type": question.question_type.value,
            "options": question.options,
            "points": question.points,
            "difficulty": question.difficulty.value,
            "has_hints": bool(question.hints)
        })
    
    return QuizResponse(
        quiz_id=quiz.quiz_id,
        title=quiz.title,
        description=quiz.description,
        mode=quiz.mode.value,
        total_questions=len(quiz.questions),
        time_limit=quiz.time_limit,
        questions=formatted_questions,
        metadata=quiz.metadata
    )


@router.post("/submit")
async def submit_quiz(submission: QuizSubmission):
    """
    Submit quiz answers for evaluation
    """
    try:
        results = await quiz_engine.evaluate_quiz(
            quiz_id=submission.quiz_id,
            answers=submission.answers
        )
        
        return {
            "success": True,
            "results": results
        }
        
    except ValueError as e:
        raise HTTPException(status_code=404, detail=str(e))
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.get("/{quiz_id}/hint/{question_index}")
async def get_hint(quiz_id: str, question_index: int):
    """
    Get hint for a specific question
    """
    hints = await quiz_engine.get_hint(quiz_id, question_index)
    if not hints:
        raise HTTPException(status_code=404, detail="No hints available")
    
    return {
        "quiz_id": quiz_id,
        "question_index": question_index,
        "hints": hints
    }


@router.get("/{quiz_id}/analytics")
async def get_quiz_analytics(quiz_id: str):
    """
    Get analytics for a quiz
    """
    analytics = await quiz_engine.get_quiz_analytics(quiz_id)
    if not analytics:
        raise HTTPException(status_code=404, detail="Quiz not found")
    
    return analytics


@router.post("/adaptive/generate")
async def generate_adaptive_quiz(
    topic: str = Query(..., description="Quiz topic"),
    user_level: str = Query("intermediate", description="User level"),
    previous_score: Optional[float] = Query(None, description="Previous quiz score")
):
    """
    Generate adaptive quiz based on user performance
    """
    try:
        # Simulate previous performance
        previous_performance = {
            "average_score": previous_score or 70,
            "user_level": user_level
        }
        
        # Generate adaptive questions
        context = f"Educational content for {topic}"
        questions = await quiz_engine.llm_service.generate_adaptive_questions(
            context=context,
            topic=topic,
            user_level=user_level,
            previous_performance=previous_performance
        )
        
        # Create quiz
        quiz = {
            "quiz_id": "adaptive_" + str(uuid.uuid4()),
            "title": f"Adaptive Quiz: {topic}",
            "mode": "adaptive",
            "questions": [q.dict() for q in questions],
            "metadata": {
                "user_level": user_level,
                "previous_score": previous_score
            }
        }
        
        return quiz
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.get("/topics/available")
async def get_available_topics(
    subject: Optional[str] = Query(None, description="Filter by subject"),
    grade_level: Optional[str] = Query(None, description="Filter by grade level")
):
    """
    Get list of available topics from uploaded content
    """
    # This would query the database for available topics
    # For now, return sample topics
    topics = [
        {
            "topic": "Mathematics",
            "subtopics": ["Algebra", "Geometry", "Calculus", "Statistics"],
            "grade_levels": ["High School", "College"],
            "question_count": 150
        },
        {
            "topic": "Science",
            "subtopics": ["Physics", "Chemistry", "Biology", "Earth Science"],
            "grade_levels": ["Middle School", "High School"],
            "question_count": 200
        },
        {
            "topic": "History",
            "subtopics": ["World History", "US History", "Ancient Civilizations"],
            "grade_levels": ["Middle School", "High School"],
            "question_count": 120
        },
        {
            "topic": "Computer Science",
            "subtopics": ["Programming", "Data Structures", "Algorithms", "Databases"],
            "grade_levels": ["High School", "College"],
            "question_count": 180
        }
    ]
    
    # Filter if needed
    if subject:
        topics = [t for t in topics if t["topic"].lower() == subject.lower()]
    
    return {
        "topics": topics,
        "total": len(topics)
    }


@router.post("/validate-answer")
async def validate_answer(
    question: str,
    user_answer: str,
    correct_answer: str,
    context: Optional[str] = None
):
    """
    Validate a single answer using LLM
    """
    try:
        result = await quiz_engine.llm_service.validate_answer(
            question=question,
            user_answer=user_answer,
            correct_answer=correct_answer,
            context=context
        )
        
        return result
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))