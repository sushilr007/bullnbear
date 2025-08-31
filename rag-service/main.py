from fastapi import FastAPI, HTTPException, UploadFile, File, Depends
from fastapi.middleware.cors import CORSMiddleware
from contextlib import asynccontextmanager
import httpx
import os
import json
from typing import List, Dict, Any
from loguru import logger
import chromadb
from chromadb.config import Settings
from sentence_transformers import SentenceTransformer
import PyPDF2
from docx import Document
import io
import re

# Import schemas
import sys
sys.path.append('../shared')
from models.schemas import *

# ChromaDB configuration
CHROMA_URL = os.getenv("CHROMA_URL", "http://localhost:8000")
chroma_client = chromadb.HttpClient(host=CHROMA_URL, port=8000)

# Sentence transformer for embeddings
embedding_model = SentenceTransformer('all-MiniLM-L6-v2')

# Content Management Service URL
CONTENT_SERVICE_URL = os.getenv("CONTENT_SERVICE_URL", "http://content-management:8000")

@asynccontextmanager
async def lifespan(app: FastAPI):
    # Startup
    logger.info("Starting RAG Service...")
    # Initialize ChromaDB collection
    try:
        global collection
        collection = chroma_client.get_or_create_collection(
            name="quiz_documents",
            metadata={"hnsw:space": "cosine"}
        )
        logger.info("ChromaDB collection initialized successfully")
    except Exception as e:
        logger.error(f"Failed to initialize ChromaDB: {e}")
    yield
    # Shutdown
    logger.info("Shutting down RAG Service...")

app = FastAPI(
    title="RAG Service",
    description="Document processing and retrieval for quiz generation",
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

class DocumentProcessor:
    """Handles document processing and chunking"""
    
    @staticmethod
    def chunk_text(text: str, chunk_size: int = 1000, overlap: int = 200) -> List[str]:
        """Split text into overlapping chunks"""
        chunks = []
        start = 0
        
        while start < len(text):
            end = start + chunk_size
            chunk = text[start:end]
            
            # Try to break at sentence boundary
            if end < len(text):
                last_period = chunk.rfind('.')
                last_question = chunk.rfind('?')
                last_exclamation = chunk.rfind('!')
                last_break = max(last_period, last_question, last_exclamation)
                
                if last_break > start + chunk_size * 0.7:  # Only break if it's not too early
                    end = start + last_break + 1
                    chunk = text[start:end]
            
            chunks.append(chunk.strip())
            start = end - overlap
            
            if start >= len(text):
                break
        
        return chunks
    
    @staticmethod
    def extract_text_from_pdf(file_content: bytes) -> str:
        """Extract text from PDF file"""
        try:
            pdf_reader = PyPDF2.PdfReader(io.BytesIO(file_content))
            text = ""
            for page in pdf_reader.pages:
                text += page.extract_text() + "\n"
            return text
        except Exception as e:
            logger.error(f"Error extracting text from PDF: {e}")
            raise HTTPException(status_code=400, detail="Invalid PDF file")
    
    @staticmethod
    def extract_text_from_docx(file_content: bytes) -> str:
        """Extract text from DOCX file"""
        try:
            doc = Document(io.BytesIO(file_content))
            text = ""
            for paragraph in doc.paragraphs:
                text += paragraph.text + "\n"
            return text
        except Exception as e:
            logger.error(f"Error extracting text from DOCX: {e}")
            raise HTTPException(status_code=400, detail="Invalid DOCX file")

class RAGService:
    """Main RAG service for document processing and retrieval"""
    
    def __init__(self):
        self.processor = DocumentProcessor()
    
    async def process_document(self, book_id: int, content: str, file_url: str = None) -> Dict[str, Any]:
        """Process document and store chunks in vector database"""
        try:
            # Clean and preprocess text
            cleaned_text = self._clean_text(content)
            
            # Chunk the text
            chunks = self.processor.chunk_text(cleaned_text)
            
            # Generate embeddings and store in ChromaDB
            embeddings = embedding_model.encode(chunks)
            
            # Prepare documents for ChromaDB
            documents = []
            metadatas = []
            ids = []
            
            for i, chunk in enumerate(chunks):
                chunk_id = f"book_{book_id}_chunk_{i}"
                documents.append(chunk)
                metadatas.append({
                    "book_id": book_id,
                    "chunk_index": i,
                    "source": file_url or "manual_upload",
                    "length": len(chunk)
                })
                ids.append(chunk_id)
            
            # Add to ChromaDB collection
            collection.add(
                documents=documents,
                embeddings=embeddings.tolist(),
                metadatas=metadatas,
                ids=ids
            )
            
            logger.info(f"Processed {len(chunks)} chunks for book {book_id}")
            
            return {
                "book_id": book_id,
                "chunks_created": len(chunks),
                "status": "success",
                "processing_time": 0.0  # TODO: Add actual timing
            }
            
        except Exception as e:
            logger.error(f"Error processing document: {e}")
            raise HTTPException(status_code=500, detail=f"Document processing failed: {str(e)}")
    
    async def retrieve_relevant_chunks(self, query: str, book_id: int = None, top_k: int = 5) -> List[Dict[str, Any]]:
        """Retrieve relevant document chunks based on query"""
        try:
            # Generate query embedding
            query_embedding = embedding_model.encode([query])
            
            # Prepare where clause for filtering
            where_clause = {}
            if book_id:
                where_clause["book_id"] = book_id
            
            # Query ChromaDB
            results = collection.query(
                query_embeddings=query_embedding.tolist(),
                n_results=top_k,
                where=where_clause if where_clause else None
            )
            
            # Format results
            chunks = []
            for i in range(len(results['documents'][0])):
                chunk = {
                    "content": results['documents'][0][i],
                    "metadata": results['metadatas'][0][i],
                    "distance": results['distances'][0][i]
                }
                chunks.append(chunk)
            
            return chunks
            
        except Exception as e:
            logger.error(f"Error retrieving chunks: {e}")
            raise HTTPException(status_code=500, detail=f"Chunk retrieval failed: {str(e)}")
    
    async def generate_questions_from_context(self, context: str, num_questions: int = 5, 
                                            question_types: List[str] = ["mcq"]) -> List[Dict[str, Any]]:
        """Generate questions from retrieved context using LLM"""
        try:
            # This would typically call an LLM service
            # For now, return a placeholder
            questions = []
            
            # TODO: Implement actual LLM integration
            # This is a placeholder implementation
            for i in range(num_questions):
                question = {
                    "question_text": f"Sample question {i+1} based on context",
                    "question_type": question_types[i % len(question_types)],
                    "difficulty": "medium",
                    "points": 1,
                    "explanation": "Sample explanation",
                    "answers": [
                        {"answer_text": "Option A", "is_correct": True},
                        {"answer_text": "Option B", "is_correct": False},
                        {"answer_text": "Option C", "is_correct": False},
                        {"answer_text": "Option D", "is_correct": False}
                    ],
                    "context_used": context[:200] + "..." if len(context) > 200 else context
                }
                questions.append(question)
            
            return questions
            
        except Exception as e:
            logger.error(f"Error generating questions: {e}")
            raise HTTPException(status_code=500, detail=f"Question generation failed: {str(e)}")
    
    def _clean_text(self, text: str) -> str:
        """Clean and preprocess text"""
        # Remove extra whitespace
        text = re.sub(r'\s+', ' ', text)
        
        # Remove special characters but keep punctuation
        text = re.sub(r'[^\w\s\.\,\!\?\;\:\-\(\)]', '', text)
        
        # Normalize whitespace
        text = ' '.join(text.split())
        
        return text.strip()

# Initialize RAG service
rag_service = RAGService()

# Health check
@app.get("/health")
async def health_check():
    """Health check endpoint"""
    return {"status": "healthy", "service": "rag-service"}

# Document upload and processing
@app.post("/documents/upload", response_model=DocumentProcessResponse)
async def upload_document(request: DocumentUploadRequest):
    """Upload and process document for RAG"""
    try:
        if request.content:
            result = await rag_service.process_document(
                book_id=request.book_id,
                content=request.content,
                file_url=request.file_url
            )
            return DocumentProcessResponse(**result)
        else:
            raise HTTPException(status_code=400, detail="Content is required")
    except Exception as e:
        logger.error(f"Upload error: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/documents/upload-file")
async def upload_file(
    book_id: int,
    file: UploadFile = File(...)
):
    """Upload file and process for RAG"""
    try:
        # Read file content
        content = await file.read()
        
        # Extract text based on file type
        if file.filename.endswith('.pdf'):
            text = DocumentProcessor.extract_text_from_pdf(content)
        elif file.filename.endswith('.docx'):
            text = DocumentProcessor.extract_text_from_docx(content)
        elif file.filename.endswith('.txt'):
            text = content.decode('utf-8')
        else:
            raise HTTPException(status_code=400, detail="Unsupported file type")
        
        # Process the extracted text
        result = await rag_service.process_document(
            book_id=book_id,
            content=text,
            file_url=file.filename
        )
        
        return DocumentProcessResponse(**result)
        
    except Exception as e:
        logger.error(f"File upload error: {e}")
        raise HTTPException(status_code=500, detail=str(e))

# Document retrieval
@app.post("/documents/retrieve")
async def retrieve_chunks(
    query: str,
    book_id: int = None,
    top_k: int = 5
):
    """Retrieve relevant document chunks"""
    try:
        chunks = await rag_service.retrieve_relevant_chunks(
            query=query,
            book_id=book_id,
            top_k=top_k
        )
        return {"chunks": chunks, "query": query, "total": len(chunks)}
    except Exception as e:
        logger.error(f"Retrieval error: {e}")
        raise HTTPException(status_code=500, detail=str(e))

# Question generation from context
@app.post("/questions/generate")
async def generate_questions(
    context: str,
    num_questions: int = 5,
    question_types: List[str] = ["mcq"]
):
    """Generate questions from context"""
    try:
        questions = await rag_service.generate_questions_from_context(
            context=context,
            num_questions=num_questions,
            question_types=question_types
        )
        return {"questions": questions, "total": len(questions)}
    except Exception as e:
        logger.error(f"Question generation error: {e}")
        raise HTTPException(status_code=500, detail=str(e))

# Combined RAG quiz generation
@app.post("/quiz/generate")
async def generate_rag_quiz(
    request: QuizGenerationRequest
):
    """Generate quiz using RAG approach"""
    try:
        # Build query based on request
        if request.book_id and request.chapter_id:
            query = f"Generate questions for chapter {request.chapter_id}"
        elif request.topic:
            query = f"Generate questions about {request.topic}"
        else:
            query = "Generate general questions"
        
        # Retrieve relevant chunks
        chunks = await rag_service.retrieve_relevant_chunks(
            query=query,
            book_id=request.book_id,
            top_k=10
        )
        
        # Combine chunks into context
        context = "\n\n".join([chunk["content"] for chunk in chunks])
        
        # Generate questions
        questions = await rag_service.generate_questions_from_context(
            context=context,
            num_questions=request.num_questions,
            question_types=[qt.value for qt in request.question_types]
        )
        
        return {
            "quiz_type": "rag",
            "questions": questions,
            "context_chunks": len(chunks),
            "total_questions": len(questions)
        }
        
    except Exception as e:
        logger.error(f"RAG quiz generation error: {e}")
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)