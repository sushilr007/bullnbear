"""
Application configuration settings
"""

from typing import List, Optional
from pydantic_settings import BaseSettings
from pydantic import Field


class Settings(BaseSettings):
    """Application settings"""
    
    # Application
    APP_NAME: str = "Quiz Engine"
    APP_VERSION: str = "1.0.0"
    DEBUG: bool = Field(default=False, env="DEBUG")
    
    # API
    API_V1_PREFIX: str = "/api/v1"
    ALLOWED_ORIGINS: List[str] = Field(
        default=["http://localhost:3000", "http://localhost:8000"],
        env="ALLOWED_ORIGINS"
    )
    
    # Database
    DATABASE_URL: str = Field(
        default="postgresql://user:password@localhost/quizdb",
        env="DATABASE_URL"
    )
    MONGODB_URL: str = Field(
        default="mongodb://localhost:27017/quizdb",
        env="MONGODB_URL"
    )
    REDIS_URL: str = Field(
        default="redis://localhost:6379/0",
        env="REDIS_URL"
    )
    
    # Vector Database
    VECTOR_DB_TYPE: str = Field(default="chromadb", env="VECTOR_DB_TYPE")
    CHROMADB_HOST: Optional[str] = Field(default="localhost", env="CHROMADB_HOST")
    CHROMADB_PORT: Optional[int] = Field(default=8000, env="CHROMADB_PORT")
    PINECONE_API_KEY: Optional[str] = Field(default=None, env="PINECONE_API_KEY")
    PINECONE_ENVIRONMENT: Optional[str] = Field(default=None, env="PINECONE_ENVIRONMENT")
    
    # LLM Configuration
    OPENAI_API_KEY: Optional[str] = Field(default=None, env="OPENAI_API_KEY")
    ANTHROPIC_API_KEY: Optional[str] = Field(default=None, env="ANTHROPIC_API_KEY")
    LLM_MODEL: str = Field(default="gpt-3.5-turbo", env="LLM_MODEL")
    LLM_TEMPERATURE: float = Field(default=0.7, env="LLM_TEMPERATURE")
    
    # Embedding Configuration
    EMBEDDING_MODEL: str = Field(
        default="sentence-transformers/all-MiniLM-L6-v2",
        env="EMBEDDING_MODEL"
    )
    EMBEDDING_DIMENSION: int = Field(default=384, env="EMBEDDING_DIMENSION")
    
    # Authentication
    SECRET_KEY: str = Field(
        default="your-secret-key-change-this-in-production",
        env="SECRET_KEY"
    )
    ALGORITHM: str = "HS256"
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 30
    REFRESH_TOKEN_EXPIRE_DAYS: int = 7
    
    # File Upload
    MAX_UPLOAD_SIZE: int = Field(default=10485760, env="MAX_UPLOAD_SIZE")  # 10MB
    ALLOWED_EXTENSIONS: List[str] = Field(
        default=[".pdf", ".docx", ".txt", ".md"],
        env="ALLOWED_EXTENSIONS"
    )
    
    # Quiz Configuration
    DEFAULT_QUESTIONS_PER_QUIZ: int = Field(default=10, env="DEFAULT_QUESTIONS_PER_QUIZ")
    MAX_QUESTIONS_PER_QUIZ: int = Field(default=50, env="MAX_QUESTIONS_PER_QUIZ")
    QUIZ_TIME_LIMIT_MINUTES: int = Field(default=30, env="QUIZ_TIME_LIMIT_MINUTES")
    
    # RAG Configuration
    CHUNK_SIZE: int = Field(default=1000, env="CHUNK_SIZE")
    CHUNK_OVERLAP: int = Field(default=200, env="CHUNK_OVERLAP")
    TOP_K_RESULTS: int = Field(default=5, env="TOP_K_RESULTS")
    
    # Celery Configuration
    CELERY_BROKER_URL: str = Field(
        default="redis://localhost:6379/1",
        env="CELERY_BROKER_URL"
    )
    CELERY_RESULT_BACKEND: str = Field(
        default="redis://localhost:6379/2",
        env="CELERY_RESULT_BACKEND"
    )
    
    # Logging
    LOG_LEVEL: str = Field(default="INFO", env="LOG_LEVEL")
    LOG_FORMAT: str = "%(asctime)s - %(name)s - %(levelname)s - %(message)s"
    
    class Config:
        env_file = ".env"
        case_sensitive = True


# Create settings instance
settings = Settings()