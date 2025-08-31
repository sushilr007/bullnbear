from fastapi import FastAPI, HTTPException, Depends, status
from fastapi.middleware.cors import CORSMiddleware
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from contextlib import asynccontextmanager
import httpx
import os
from typing import Dict, Any
import json
from loguru import logger

# Import schemas
import sys
sys.path.append('../shared')
from models.schemas import *

# Security
security = HTTPBearer()

# Service URLs
SERVICES = {
    "user_management": os.getenv("USER_MANAGEMENT_URL", "http://user-management:8000"),
    "quiz_generation": os.getenv("QUIZ_GENERATION_URL", "http://quiz-generation:8000"),
    "rag_service": os.getenv("RAG_SERVICE_URL", "http://rag-service:8000"),
    "llm_service": os.getenv("LLM_SERVICE_URL", "http://llm-service:8000"),
    "content_management": os.getenv("CONTENT_MANAGEMENT_URL", "http://content-management:8000"),
    "quiz_session": os.getenv("QUIZ_SESSION_URL", "http://quiz-session:8000"),
}

@asynccontextmanager
async def lifespan(app: FastAPI):
    # Startup
    logger.info("Starting API Gateway...")
    yield
    # Shutdown
    logger.info("Shutting down API Gateway...")

app = FastAPI(
    title="Quiz Engine API Gateway",
    description="Dynamic Quiz Engine with RAG and LLM support",
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

# Authentication dependency
async def get_current_user(credentials: HTTPAuthorizationCredentials = Depends(security)) -> Dict[str, Any]:
    """Validate JWT token and return user info"""
    try:
        async with httpx.AsyncClient() as client:
            response = await client.post(
                f"{SERVICES['user_management']}/auth/verify",
                headers={"Authorization": f"Bearer {credentials.credentials}"}
            )
            if response.status_code == 200:
                return response.json()
            else:
                raise HTTPException(
                    status_code=status.HTTP_401_UNAUTHORIZED,
                    detail="Invalid authentication credentials"
                )
    except Exception as e:
        logger.error(f"Authentication error: {e}")
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Authentication failed"
        )

# Health check
@app.get("/health")
async def health_check():
    """Health check endpoint"""
    return {"status": "healthy", "service": "api-gateway"}

# User Management Routes
@app.post("/auth/register", response_model=UserResponse)
async def register_user(user_data: UserCreate):
    """Register a new user"""
    async with httpx.AsyncClient() as client:
        response = await client.post(
            f"{SERVICES['user_management']}/auth/register",
            json=user_data.model_dump()
        )
        return response.json()

@app.post("/auth/login", response_model=Token)
async def login_user(user_data: UserLogin):
    """Login user"""
    async with httpx.AsyncClient() as client:
        response = await client.post(
            f"{SERVICES['user_management']}/auth/login",
            json=user_data.model_dump()
        )
        return response.json()

@app.get("/users/me", response_model=UserResponse)
async def get_current_user_info(current_user: Dict[str, Any] = Depends(get_current_user)):
    """Get current user information"""
    async with httpx.AsyncClient() as client:
        response = await client.get(
            f"{SERVICES['user_management']}/users/me",
            headers={"Authorization": f"Bearer {current_user['token']}"}
        )
        return response.json()

# Content Management Routes
@app.post("/books", response_model=BookResponse)
async def create_book(book_data: BookCreate, current_user: Dict[str, Any] = Depends(get_current_user)):
    """Create a new book"""
    async with httpx.AsyncClient() as client:
        response = await client.post(
            f"{SERVICES['content_management']}/books",
            json=book_data.model_dump(),
            headers={"Authorization": f"Bearer {current_user['token']}"}
        )
        return response.json()

@app.get("/books", response_model=PaginatedResponse)
async def get_books(
    page: int = 1,
    size: int = 10,
    subject: str = None,
    grade_level: str = None,
    current_user: Dict[str, Any] = Depends(get_current_user)
):
    """Get books with pagination and filters"""
    params = {"page": page, "size": size}
    if subject:
        params["subject"] = subject
    if grade_level:
        params["grade_level"] = grade_level
    
    async with httpx.AsyncClient() as client:
        response = await client.get(
            f"{SERVICES['content_management']}/books",
            params=params,
            headers={"Authorization": f"Bearer {current_user['token']}"}
        )
        return response.json()

@app.get("/books/{book_id}", response_model=BookResponse)
async def get_book(book_id: int, current_user: Dict[str, Any] = Depends(get_current_user)):
    """Get book by ID"""
    async with httpx.AsyncClient() as client:
        response = await client.get(
            f"{SERVICES['content_management']}/books/{book_id}",
            headers={"Authorization": f"Bearer {current_user['token']}"}
        )
        return response.json()

# Quiz Generation Routes
@app.post("/quizzes/generate", response_model=QuizGenerationResponse)
async def generate_quiz(
    request: QuizGenerationRequest,
    current_user: Dict[str, Any] = Depends(get_current_user)
):
    """Generate a new quiz using RAG, LLM, or hybrid approach"""
    async with httpx.AsyncClient() as client:
        response = await client.post(
            f"{SERVICES['quiz_generation']}/generate",
            json=request.model_dump(),
            headers={"Authorization": f"Bearer {current_user['token']}"}
        )
        return response.json()

@app.get("/quizzes", response_model=PaginatedResponse)
async def get_quizzes(
    page: int = 1,
    size: int = 10,
    book_id: int = None,
    quiz_type: QuizType = None,
    current_user: Dict[str, Any] = Depends(get_current_user)
):
    """Get quizzes with pagination and filters"""
    params = {"page": page, "size": size}
    if book_id:
        params["book_id"] = book_id
    if quiz_type:
        params["quiz_type"] = quiz_type
    
    async with httpx.AsyncClient() as client:
        response = await client.get(
            f"{SERVICES['quiz_generation']}/quizzes",
            params=params,
            headers={"Authorization": f"Bearer {current_user['token']}"}
        )
        return response.json()

@app.get("/quizzes/{quiz_id}", response_model=QuizResponse)
async def get_quiz(quiz_id: int, current_user: Dict[str, Any] = Depends(get_current_user)):
    """Get quiz by ID"""
    async with httpx.AsyncClient() as client:
        response = await client.get(
            f"{SERVICES['quiz_generation']}/quizzes/{quiz_id}",
            headers={"Authorization": f"Bearer {current_user['token']}"}
        )
        return response.json()

# Quiz Session Routes
@app.post("/sessions", response_model=QuizSessionResponse)
async def create_session(
    session_data: QuizSessionCreate,
    current_user: Dict[str, Any] = Depends(get_current_user)
):
    """Start a new quiz session"""
    async with httpx.AsyncClient() as client:
        response = await client.post(
            f"{SERVICES['quiz_session']}/sessions",
            json=session_data.model_dump(),
            headers={"Authorization": f"Bearer {current_user['token']}"}
        )
        return response.json()

@app.put("/sessions/{session_id}", response_model=QuizSessionResponse)
async def update_session(
    session_id: int,
    session_data: QuizSessionUpdate,
    current_user: Dict[str, Any] = Depends(get_current_user)
):
    """Update quiz session (submit answers, complete, etc.)"""
    async with httpx.AsyncClient() as client:
        response = await client.put(
            f"{SERVICES['quiz_session']}/sessions/{session_id}",
            json=session_data.model_dump(),
            headers={"Authorization": f"Bearer {current_user['token']}"}
        )
        return response.json()

@app.get("/sessions/{session_id}", response_model=QuizSessionResponse)
async def get_session(session_id: int, current_user: Dict[str, Any] = Depends(get_current_user)):
    """Get quiz session by ID"""
    async with httpx.AsyncClient() as client:
        response = await client.get(
            f"{SERVICES['quiz_session']}/sessions/{session_id}",
            headers={"Authorization": f"Bearer {current_user['token']}"}
        )
        return response.json()

# RAG Service Routes
@app.post("/documents/upload", response_model=DocumentProcessResponse)
async def upload_document(
    request: DocumentUploadRequest,
    current_user: Dict[str, Any] = Depends(get_current_user)
):
    """Upload and process document for RAG"""
    async with httpx.AsyncClient() as client:
        response = await client.post(
            f"{SERVICES['rag_service']}/documents/upload",
            json=request.model_dump(),
            headers={"Authorization": f"Bearer {current_user['token']}"}
        )
        return response.json()

# LLM Service Routes
@app.post("/llm/generate", response_model=QuizGenerationResponse)
async def generate_llm_quiz(
    request: LLMGenerationRequest,
    current_user: Dict[str, Any] = Depends(get_current_user)
):
    """Generate quiz using LLM directly"""
    async with httpx.AsyncClient() as client:
        response = await client.post(
            f"{SERVICES['llm_service']}/generate",
            json=request.model_dump(),
            headers={"Authorization": f"Bearer {current_user['token']}"}
        )
        return response.json()

# Progress Routes
@app.get("/progress", response_model=List[UserProgressResponse])
async def get_user_progress(current_user: Dict[str, Any] = Depends(get_current_user)):
    """Get user's quiz progress"""
    async with httpx.AsyncClient() as client:
        response = await client.get(
            f"{SERVICES['quiz_session']}/progress",
            headers={"Authorization": f"Bearer {current_user['token']}"}
        )
        return response.json()

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)