"""
Database models for Quiz Engine
"""

from datetime import datetime
from typing import Optional, List, Dict, Any
from sqlalchemy import Column, String, Integer, Float, DateTime, JSON, Text, Boolean, ForeignKey, Enum
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import relationship
import enum

Base = declarative_base()


class UserRole(str, enum.Enum):
    """User roles"""
    STUDENT = "student"
    TEACHER = "teacher"
    ADMIN = "admin"


class User(Base):
    """User model"""
    __tablename__ = "users"
    
    id = Column(Integer, primary_key=True, index=True)
    username = Column(String(100), unique=True, index=True, nullable=False)
    email = Column(String(255), unique=True, index=True, nullable=False)
    hashed_password = Column(String(255), nullable=False)
    full_name = Column(String(255))
    role = Column(Enum(UserRole), default=UserRole.STUDENT)
    is_active = Column(Boolean, default=True)
    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
    
    # Relationships
    quizzes_created = relationship("QuizDB", back_populates="creator", foreign_keys="QuizDB.creator_id")
    quiz_attempts = relationship("QuizAttempt", back_populates="user")
    uploaded_content = relationship("Content", back_populates="uploader")


class QuizDB(Base):
    """Quiz database model"""
    __tablename__ = "quizzes"
    
    id = Column(Integer, primary_key=True, index=True)
    quiz_id = Column(String(100), unique=True, index=True, nullable=False)
    title = Column(String(255), nullable=False)
    description = Column(Text)
    mode = Column(String(50), nullable=False)  # rag_based, llm_based, hybrid, question_bank
    topic = Column(String(255), index=True)
    subject = Column(String(100), index=True)
    grade_level = Column(String(50))
    difficulty = Column(String(50))
    time_limit = Column(Integer)  # in minutes
    total_questions = Column(Integer)
    total_points = Column(Integer)
    passing_score = Column(Float, default=60.0)  # percentage
    
    # Metadata
    questions = Column(JSON)  # Store questions as JSON
    settings = Column(JSON)  # Additional quiz settings
    
    # Status and timestamps
    status = Column(String(50), default="draft")  # draft, active, completed, expired
    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
    expires_at = Column(DateTime)
    
    # Relationships
    creator_id = Column(Integer, ForeignKey("users.id"))
    creator = relationship("User", back_populates="quizzes_created", foreign_keys=[creator_id])
    attempts = relationship("QuizAttempt", back_populates="quiz")


class QuizAttempt(Base):
    """Quiz attempt/submission model"""
    __tablename__ = "quiz_attempts"
    
    id = Column(Integer, primary_key=True, index=True)
    attempt_id = Column(String(100), unique=True, index=True, nullable=False)
    
    # Foreign keys
    quiz_id = Column(Integer, ForeignKey("quizzes.id"), nullable=False)
    user_id = Column(Integer, ForeignKey("users.id"), nullable=False)
    
    # Attempt details
    started_at = Column(DateTime, default=datetime.utcnow)
    completed_at = Column(DateTime)
    time_taken = Column(Integer)  # in seconds
    
    # Scoring
    score = Column(Float)
    max_score = Column(Float)
    percentage = Column(Float)
    passed = Column(Boolean)
    
    # Answers and feedback
    answers = Column(JSON)  # User's answers
    evaluation = Column(JSON)  # Detailed evaluation results
    feedback = Column(Text)
    
    # Relationships
    quiz = relationship("QuizDB", back_populates="attempts")
    user = relationship("User", back_populates="quiz_attempts")


class Question(Base):
    """Question bank model"""
    __tablename__ = "questions"
    
    id = Column(Integer, primary_key=True, index=True)
    question_id = Column(String(100), unique=True, index=True, nullable=False)
    
    # Question content
    question_text = Column(Text, nullable=False)
    question_type = Column(String(50), nullable=False)  # multiple_choice, true_false, etc.
    options = Column(JSON)  # For multiple choice questions
    correct_answer = Column(Text, nullable=False)
    explanation = Column(Text)
    hints = Column(JSON)
    
    # Metadata
    topic = Column(String(255), index=True)
    subtopic = Column(String(255))
    subject = Column(String(100), index=True)
    grade_level = Column(String(50))
    difficulty = Column(String(50))
    points = Column(Integer, default=10)
    
    # Usage tracking
    times_used = Column(Integer, default=0)
    times_correct = Column(Integer, default=0)
    average_time = Column(Float)  # Average time to answer in seconds
    
    # Source
    source = Column(String(255))  # Book, document, or generated
    source_page = Column(String(50))
    
    # Timestamps
    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
    
    # Relationships
    content_id = Column(Integer, ForeignKey("content.id"))
    content = relationship("Content", back_populates="questions")


class Content(Base):
    """Educational content model"""
    __tablename__ = "content"
    
    id = Column(Integer, primary_key=True, index=True)
    content_id = Column(String(100), unique=True, index=True, nullable=False)
    
    # Content details
    title = Column(String(255), nullable=False)
    description = Column(Text)
    file_path = Column(String(500))
    file_type = Column(String(50))  # pdf, docx, txt, etc.
    file_size = Column(Integer)  # in bytes
    
    # Categorization
    subject = Column(String(100), index=True)
    grade_level = Column(String(50))
    topic = Column(String(255), index=True)
    tags = Column(JSON)
    
    # Processing status
    processing_status = Column(String(50), default="pending")  # pending, processing, completed, failed
    processed_at = Column(DateTime)
    error_message = Column(Text)
    
    # Vector storage info
    vector_db_id = Column(String(255))  # Document ID in vector database
    total_chunks = Column(Integer)
    embedding_model = Column(String(100))
    
    # Metadata
    metadata = Column(JSON)
    
    # Timestamps
    uploaded_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
    
    # Relationships
    uploader_id = Column(Integer, ForeignKey("users.id"))
    uploader = relationship("User", back_populates="uploaded_content")
    questions = relationship("Question", back_populates="content")


class Analytics(Base):
    """Analytics and performance tracking"""
    __tablename__ = "analytics"
    
    id = Column(Integer, primary_key=True, index=True)
    
    # Reference
    user_id = Column(Integer, ForeignKey("users.id"))
    quiz_id = Column(Integer, ForeignKey("quizzes.id"))
    question_id = Column(Integer, ForeignKey("questions.id"))
    
    # Metrics
    event_type = Column(String(50))  # quiz_started, quiz_completed, question_answered, etc.
    event_data = Column(JSON)
    
    # Performance metrics
    score = Column(Float)
    time_spent = Column(Integer)  # in seconds
    attempts = Column(Integer)
    
    # Timestamps
    created_at = Column(DateTime, default=datetime.utcnow)


class QuizTemplate(Base):
    """Quiz template for reusable quiz configurations"""
    __tablename__ = "quiz_templates"
    
    id = Column(Integer, primary_key=True, index=True)
    template_id = Column(String(100), unique=True, index=True, nullable=False)
    
    # Template details
    name = Column(String(255), nullable=False)
    description = Column(Text)
    
    # Configuration
    mode = Column(String(50))
    question_types = Column(JSON)  # Distribution of question types
    difficulty_distribution = Column(JSON)  # Distribution of difficulties
    topics = Column(JSON)  # List of topics to cover
    
    # Settings
    num_questions = Column(Integer)
    time_limit = Column(Integer)
    passing_score = Column(Float)
    randomize_questions = Column(Boolean, default=True)
    show_explanations = Column(Boolean, default=True)
    allow_hints = Column(Boolean, default=True)
    
    # Metadata
    subject = Column(String(100))
    grade_level = Column(String(50))
    tags = Column(JSON)
    
    # Usage
    times_used = Column(Integer, default=0)
    average_score = Column(Float)
    
    # Timestamps
    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
    
    # Creator
    creator_id = Column(Integer, ForeignKey("users.id"))


class StudySession(Base):
    """Study session tracking"""
    __tablename__ = "study_sessions"
    
    id = Column(Integer, primary_key=True, index=True)
    session_id = Column(String(100), unique=True, index=True, nullable=False)
    
    # User and content
    user_id = Column(Integer, ForeignKey("users.id"), nullable=False)
    content_id = Column(Integer, ForeignKey("content.id"))
    
    # Session details
    topic = Column(String(255))
    duration = Column(Integer)  # in seconds
    pages_viewed = Column(Integer)
    questions_practiced = Column(Integer)
    
    # Performance
    correct_answers = Column(Integer)
    incorrect_answers = Column(Integer)
    accuracy = Column(Float)
    
    # Timestamps
    started_at = Column(DateTime, default=datetime.utcnow)
    ended_at = Column(DateTime)
    
    # Notes
    notes = Column(Text)
    bookmarks = Column(JSON)