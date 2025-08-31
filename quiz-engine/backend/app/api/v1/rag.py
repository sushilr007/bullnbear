"""
RAG (Retrieval-Augmented Generation) API endpoints
"""

from fastapi import APIRouter, HTTPException, UploadFile, File, Form, Query
from typing import List, Dict, Any, Optional
import os
import aiofiles
from pathlib import Path

from app.services.rag_service import RAGService

router = APIRouter()

# Initialize RAG service
rag_service = RAGService()

# Upload directory
UPLOAD_DIR = Path("/workspace/quiz-engine/data/uploads")
UPLOAD_DIR.mkdir(parents=True, exist_ok=True)


@router.post("/upload")
async def upload_document(
    file: UploadFile = File(...),
    subject: Optional[str] = Form(None),
    grade_level: Optional[str] = Form(None),
    topic: Optional[str] = Form(None),
    tags: Optional[str] = Form(None)
):
    """
    Upload and process educational content
    """
    try:
        # Validate file type
        allowed_extensions = [".pdf", ".docx", ".txt", ".md"]
        file_extension = Path(file.filename).suffix.lower()
        
        if file_extension not in allowed_extensions:
            raise HTTPException(
                status_code=400,
                detail=f"File type not supported. Allowed types: {allowed_extensions}"
            )
        
        # Save uploaded file
        file_path = UPLOAD_DIR / file.filename
        async with aiofiles.open(file_path, 'wb') as f:
            content = await file.read()
            await f.write(content)
        
        # Prepare metadata
        metadata = {
            "subject": subject,
            "grade_level": grade_level,
            "topic": topic,
            "tags": tags.split(",") if tags else [],
            "filename": file.filename,
            "file_size": len(content)
        }
        
        # Process document
        result = await rag_service.process_document(
            file_path=str(file_path),
            metadata=metadata
        )
        
        return {
            "success": result["status"] == "success",
            "document_id": result.get("document_id"),
            "filename": file.filename,
            "total_chunks": result.get("total_chunks"),
            "metadata": metadata,
            "message": "Document processed successfully" if result["status"] == "success" else result.get("error")
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/search")
async def search_content(
    query: str,
    subject: Optional[str] = None,
    grade_level: Optional[str] = None,
    top_k: int = 5
):
    """
    Search for relevant content
    """
    try:
        results = await rag_service.search_by_topic(
            topic=query,
            subject=subject,
            grade_level=grade_level,
            top_k=top_k
        )
        
        return {
            "query": query,
            "results": results,
            "total": len(results)
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.get("/document/{document_id}")
async def get_document_info(document_id: str):
    """
    Get information about a processed document
    """
    summary = await rag_service.get_document_summary(document_id)
    if not summary:
        raise HTTPException(status_code=404, detail="Document not found")
    
    return summary


@router.delete("/document/{document_id}")
async def delete_document(document_id: str):
    """
    Delete a document from the vector store
    """
    success = await rag_service.delete_document(document_id)
    if not success:
        raise HTTPException(status_code=500, detail="Failed to delete document")
    
    return {
        "success": True,
        "message": f"Document {document_id} deleted successfully"
    }


@router.post("/retrieve")
async def retrieve_context(
    query: str,
    filters: Optional[Dict[str, Any]] = None,
    top_k: int = 5
):
    """
    Retrieve relevant context for a query
    """
    try:
        results = await rag_service.retrieve_context(
            query=query,
            filters=filters,
            top_k=top_k
        )
        
        return {
            "query": query,
            "contexts": results,
            "total": len(results)
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.get("/documents")
async def list_documents(
    subject: Optional[str] = Query(None),
    grade_level: Optional[str] = Query(None),
    limit: int = Query(10, ge=1, le=100)
):
    """
    List processed documents
    """
    # This would query the database for documents
    # For now, return sample data
    documents = [
        {
            "document_id": "doc_001",
            "filename": "algebra_textbook.pdf",
            "subject": "Mathematics",
            "grade_level": "High School",
            "topic": "Algebra",
            "chunks": 150,
            "uploaded_at": "2024-01-15T10:00:00Z"
        },
        {
            "document_id": "doc_002",
            "filename": "biology_notes.docx",
            "subject": "Science",
            "grade_level": "High School",
            "topic": "Biology",
            "chunks": 200,
            "uploaded_at": "2024-01-14T15:30:00Z"
        }
    ]
    
    # Filter if needed
    if subject:
        documents = [d for d in documents if d["subject"] == subject]
    if grade_level:
        documents = [d for d in documents if d["grade_level"] == grade_level]
    
    return {
        "documents": documents[:limit],
        "total": len(documents)
    }


@router.post("/process-batch")
async def process_batch_documents(
    files: List[UploadFile] = File(...),
    subject: Optional[str] = Form(None),
    grade_level: Optional[str] = Form(None)
):
    """
    Process multiple documents in batch
    """
    results = []
    
    for file in files:
        try:
            # Save file
            file_path = UPLOAD_DIR / file.filename
            async with aiofiles.open(file_path, 'wb') as f:
                content = await file.read()
                await f.write(content)
            
            # Process document
            metadata = {
                "subject": subject,
                "grade_level": grade_level,
                "filename": file.filename
            }
            
            result = await rag_service.process_document(
                file_path=str(file_path),
                metadata=metadata
            )
            
            results.append({
                "filename": file.filename,
                "success": result["status"] == "success",
                "document_id": result.get("document_id"),
                "error": result.get("error")
            })
            
        except Exception as e:
            results.append({
                "filename": file.filename,
                "success": False,
                "error": str(e)
            })
    
    return {
        "processed": len(results),
        "successful": sum(1 for r in results if r["success"]),
        "failed": sum(1 for r in results if not r["success"]),
        "results": results
    }


@router.get("/statistics")
async def get_rag_statistics():
    """
    Get RAG system statistics
    """
    # This would query the database for actual statistics
    # For now, return sample data
    return {
        "total_documents": 25,
        "total_chunks": 3500,
        "total_embeddings": 3500,
        "vector_db_type": "chromadb",
        "embedding_model": "sentence-transformers/all-MiniLM-L6-v2",
        "subjects": ["Mathematics", "Science", "History", "Computer Science"],
        "grade_levels": ["Middle School", "High School", "College"],
        "storage_used_mb": 150.5,
        "last_document_processed": "2024-01-15T16:45:00Z"
    }