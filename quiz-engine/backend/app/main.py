"""
Main FastAPI application entry point
"""

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from contextlib import asynccontextmanager
import logging

from app.api.v1 import quiz, rag, llm, auth, content
from app.core.config import settings
from app.core.database import init_db
from app.core.logging import setup_logging

# Setup logging
setup_logging()
logger = logging.getLogger(__name__)


@asynccontextmanager
async def lifespan(app: FastAPI):
    """
    Application lifespan manager
    """
    # Startup
    logger.info("Starting Quiz Engine Application")
    await init_db()
    yield
    # Shutdown
    logger.info("Shutting down Quiz Engine Application")


# Create FastAPI app
app = FastAPI(
    title="Quiz Engine API",
    description="Dynamic Quiz Engine with RAG and LLM capabilities",
    version="1.0.0",
    lifespan=lifespan
)

# Configure CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.ALLOWED_ORIGINS,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Include routers
app.include_router(auth.router, prefix="/api/v1/auth", tags=["Authentication"])
app.include_router(quiz.router, prefix="/api/v1/quiz", tags=["Quiz"])
app.include_router(rag.router, prefix="/api/v1/rag", tags=["RAG"])
app.include_router(llm.router, prefix="/api/v1/llm", tags=["LLM"])
app.include_router(content.router, prefix="/api/v1/content", tags=["Content"])


@app.get("/")
async def root():
    """Root endpoint"""
    return {
        "message": "Quiz Engine API",
        "version": "1.0.0",
        "docs": "/docs",
        "health": "/health"
    }


@app.get("/health")
async def health_check():
    """Health check endpoint"""
    return {
        "status": "healthy",
        "service": "quiz-engine-api"
    }