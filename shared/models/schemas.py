from pydantic import BaseModel, EmailStr, Field
from typing import List, Optional, Dict, Any
from datetime import datetime
from enum import Enum

# Enums
class QuestionType(str, Enum):
    MCQ = "mcq"
    TRUE_FALSE = "true_false"
    FILL_BLANK = "fill_blank"
    ESSAY = "essay"

class DifficultyLevel(str, Enum):
    EASY = "easy"
    MEDIUM = "medium"
    HARD = "hard"

class QuizType(str, Enum):
    RAG = "rag"
    LLM = "llm"
    HYBRID = "hybrid"

class UserRole(str, Enum):
    STUDENT = "student"
    TEACHER = "teacher"
    ADMIN = "admin"

class SessionStatus(str, Enum):
    IN_PROGRESS = "in_progress"
    COMPLETED = "completed"
    ABANDONED = "abandoned"

# Base Models
class BaseSchema(BaseModel):
    class Config:
        from_attributes = True

# User Models
class UserBase(BaseSchema):
    username: str = Field(..., min_length=3, max_length=50)
    email: EmailStr
    role: UserRole = UserRole.STUDENT

class UserCreate(UserBase):
    password: str = Field(..., min_length=8)

class UserUpdate(BaseSchema):
    username: Optional[str] = Field(None, min_length=3, max_length=50)
    email: Optional[EmailStr] = None
    role: Optional[UserRole] = None
    is_active: Optional[bool] = None

class UserResponse(UserBase):
    id: int
    is_active: bool
    created_at: datetime
    updated_at: datetime

class UserLogin(BaseSchema):
    email: EmailStr
    password: str

class Token(BaseSchema):
    access_token: str
    token_type: str = "bearer"
    expires_in: int

# Book Models
class BookBase(BaseSchema):
    title: str = Field(..., min_length=1, max_length=255)
    author: Optional[str] = None
    subject: Optional[str] = None
    grade_level: Optional[str] = None
    isbn: Optional[str] = None
    description: Optional[str] = None
    cover_image_url: Optional[str] = None

class BookCreate(BookBase):
    pass

class BookUpdate(BaseSchema):
    title: Optional[str] = Field(None, min_length=1, max_length=255)
    author: Optional[str] = None
    subject: Optional[str] = None
    grade_level: Optional[str] = None
    isbn: Optional[str] = None
    description: Optional[str] = None
    cover_image_url: Optional[str] = None

class BookResponse(BookBase):
    id: int
    created_at: datetime
    updated_at: datetime

# Chapter Models
class ChapterBase(BaseSchema):
    title: str = Field(..., min_length=1, max_length=255)
    content: Optional[str] = None
    order_index: int = 0

class ChapterCreate(ChapterBase):
    book_id: int

class ChapterUpdate(BaseSchema):
    title: Optional[str] = Field(None, min_length=1, max_length=255)
    content: Optional[str] = None
    order_index: Optional[int] = None

class ChapterResponse(ChapterBase):
    id: int
    book_id: int
    created_at: datetime
    updated_at: datetime

# Quiz Models
class QuizBase(BaseSchema):
    title: str = Field(..., min_length=1, max_length=255)
    description: Optional[str] = None
    difficulty: DifficultyLevel = DifficultyLevel.MEDIUM
    quiz_type: QuizType = QuizType.RAG
    time_limit: Optional[int] = None  # in minutes

class QuizCreate(QuizBase):
    book_id: Optional[int] = None
    chapter_id: Optional[int] = None

class QuizUpdate(BaseSchema):
    title: Optional[str] = Field(None, min_length=1, max_length=255)
    description: Optional[str] = None
    difficulty: Optional[DifficultyLevel] = None
    quiz_type: Optional[QuizType] = None
    time_limit: Optional[int] = None

class QuizResponse(QuizBase):
    id: int
    book_id: Optional[int]
    chapter_id: Optional[int]
    total_points: int
    created_by: Optional[int]
    created_at: datetime
    updated_at: datetime

# Question Models
class QuestionBase(BaseSchema):
    question_text: str
    question_type: QuestionType = QuestionType.MCQ
    difficulty: DifficultyLevel = DifficultyLevel.MEDIUM
    points: int = 1
    explanation: Optional[str] = None

class QuestionCreate(QuestionBase):
    quiz_id: int
    answers: List['AnswerCreate']

class QuestionUpdate(BaseSchema):
    question_text: Optional[str] = None
    question_type: Optional[QuestionType] = None
    difficulty: Optional[DifficultyLevel] = None
    points: Optional[int] = None
    explanation: Optional[str] = None

class QuestionResponse(QuestionBase):
    id: int
    quiz_id: int
    metadata: Optional[Dict[str, Any]] = None
    created_at: datetime

# Answer Models
class AnswerBase(BaseSchema):
    answer_text: str
    is_correct: bool = False
    explanation: Optional[str] = None
    order_index: int = 0

class AnswerCreate(AnswerBase):
    pass

class AnswerUpdate(BaseSchema):
    answer_text: Optional[str] = None
    is_correct: Optional[bool] = None
    explanation: Optional[str] = None
    order_index: Optional[int] = None

class AnswerResponse(AnswerBase):
    id: int
    question_id: int
    created_at: datetime

# Quiz Generation Models
class QuizGenerationRequest(BaseSchema):
    book_id: Optional[int] = None
    chapter_id: Optional[int] = None
    topic: Optional[str] = None
    subject: Optional[str] = None
    difficulty: DifficultyLevel = DifficultyLevel.MEDIUM
    quiz_type: QuizType = QuizType.RAG
    num_questions: int = Field(10, ge=1, le=50)
    question_types: List[QuestionType] = [QuestionType.MCQ]
    time_limit: Optional[int] = None

class QuizGenerationResponse(BaseSchema):
    quiz_id: int
    title: str
    description: str
    total_questions: int
    estimated_time: Optional[int] = None
    status: str = "generated"

# Quiz Session Models
class QuizSessionCreate(BaseSchema):
    quiz_id: int

class QuizSessionUpdate(BaseSchema):
    status: SessionStatus
    score: Optional[float] = None
    answers_data: Optional[Dict[str, Any]] = None

class QuizSessionResponse(BaseSchema):
    id: int
    user_id: int
    quiz_id: int
    start_time: datetime
    end_time: Optional[datetime]
    status: SessionStatus
    score: float
    total_questions: int
    correct_answers: int
    time_taken: Optional[int]
    created_at: datetime

# RAG Models
class DocumentUploadRequest(BaseSchema):
    book_id: int
    file_url: Optional[str] = None
    content: Optional[str] = None

class DocumentProcessResponse(BaseSchema):
    book_id: int
    chunks_created: int
    status: str
    processing_time: float

# LLM Models
class LLMGenerationRequest(BaseSchema):
    topic: str
    subject: Optional[str] = None
    difficulty: DifficultyLevel = DifficultyLevel.MEDIUM
    num_questions: int = Field(10, ge=1, le=50)
    question_types: List[QuestionType] = [QuestionType.MCQ]
    context: Optional[str] = None

# Progress Models
class UserProgressResponse(BaseSchema):
    user_id: int
    quiz_id: int
    best_score: float
    attempts: int
    last_attempt: Optional[datetime]
    average_time: Optional[float]

# API Response Models
class PaginatedResponse(BaseSchema):
    items: List[Any]
    total: int
    page: int
    size: int
    pages: int

class ErrorResponse(BaseSchema):
    error: str
    detail: Optional[str] = None
    code: Optional[str] = None

class SuccessResponse(BaseSchema):
    message: str
    data: Optional[Any] = None

# Update forward references
QuestionCreate.model_rebuild()
QuestionResponse.model_rebuild()