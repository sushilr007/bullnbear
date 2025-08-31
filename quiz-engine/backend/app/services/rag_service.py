"""
RAG (Retrieval-Augmented Generation) Service
Handles document processing, embedding generation, and retrieval
"""

import logging
from typing import List, Dict, Any, Optional
from pathlib import Path
import hashlib

from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain.embeddings import OpenAIEmbeddings, HuggingFaceEmbeddings
from langchain.vectorstores import Chroma, Pinecone
from langchain.document_loaders import (
    PyPDFLoader,
    Docx2txtLoader,
    TextLoader,
    UnstructuredMarkdownLoader
)
from langchain.schema import Document
import chromadb
from chromadb.config import Settings as ChromaSettings

from app.core.config import settings

logger = logging.getLogger(__name__)


class RAGService:
    """
    Service for managing RAG operations
    """
    
    def __init__(self):
        """Initialize RAG service with vector store and embeddings"""
        self.embeddings = self._initialize_embeddings()
        self.vector_store = self._initialize_vector_store()
        self.text_splitter = RecursiveCharacterTextSplitter(
            chunk_size=settings.CHUNK_SIZE,
            chunk_overlap=settings.CHUNK_OVERLAP,
            length_function=len,
            separators=["\n\n", "\n", " ", ""]
        )
    
    def _initialize_embeddings(self):
        """Initialize embedding model"""
        if settings.OPENAI_API_KEY:
            logger.info("Using OpenAI embeddings")
            return OpenAIEmbeddings(
                openai_api_key=settings.OPENAI_API_KEY,
                model="text-embedding-ada-002"
            )
        else:
            logger.info(f"Using HuggingFace embeddings: {settings.EMBEDDING_MODEL}")
            return HuggingFaceEmbeddings(
                model_name=settings.EMBEDDING_MODEL,
                model_kwargs={'device': 'cpu'},
                encode_kwargs={'normalize_embeddings': True}
            )
    
    def _initialize_vector_store(self):
        """Initialize vector store"""
        if settings.VECTOR_DB_TYPE == "chromadb":
            logger.info("Initializing ChromaDB vector store")
            client = chromadb.Client(ChromaSettings(
                chroma_db_impl="duckdb+parquet",
                persist_directory="./chroma_db",
                anonymized_telemetry=False
            ))
            return Chroma(
                client=client,
                collection_name="quiz_content",
                embedding_function=self.embeddings
            )
        elif settings.VECTOR_DB_TYPE == "pinecone":
            logger.info("Initializing Pinecone vector store")
            import pinecone
            pinecone.init(
                api_key=settings.PINECONE_API_KEY,
                environment=settings.PINECONE_ENVIRONMENT
            )
            return Pinecone.from_existing_index(
                index_name="quiz-content",
                embedding=self.embeddings
            )
        else:
            raise ValueError(f"Unsupported vector DB type: {settings.VECTOR_DB_TYPE}")
    
    async def process_document(
        self,
        file_path: str,
        metadata: Optional[Dict[str, Any]] = None
    ) -> Dict[str, Any]:
        """
        Process a document and store in vector database
        
        Args:
            file_path: Path to the document
            metadata: Additional metadata for the document
        
        Returns:
            Processing result with document ID and statistics
        """
        try:
            # Load document
            documents = self._load_document(file_path)
            
            # Generate document ID
            doc_id = self._generate_document_id(file_path)
            
            # Add metadata to documents
            if metadata:
                for doc in documents:
                    doc.metadata.update(metadata)
                    doc.metadata["document_id"] = doc_id
                    doc.metadata["source_file"] = file_path
            
            # Split documents into chunks
            chunks = self.text_splitter.split_documents(documents)
            logger.info(f"Split document into {len(chunks)} chunks")
            
            # Add chunk IDs
            for i, chunk in enumerate(chunks):
                chunk.metadata["chunk_id"] = f"{doc_id}_chunk_{i}"
                chunk.metadata["chunk_index"] = i
            
            # Store in vector database
            self.vector_store.add_documents(chunks)
            
            return {
                "document_id": doc_id,
                "file_path": file_path,
                "total_chunks": len(chunks),
                "status": "success",
                "metadata": metadata
            }
            
        except Exception as e:
            logger.error(f"Error processing document: {e}")
            return {
                "status": "error",
                "error": str(e),
                "file_path": file_path
            }
    
    def _load_document(self, file_path: str) -> List[Document]:
        """Load document based on file type"""
        path = Path(file_path)
        extension = path.suffix.lower()
        
        if extension == ".pdf":
            loader = PyPDFLoader(file_path)
        elif extension == ".docx":
            loader = Docx2txtLoader(file_path)
        elif extension == ".txt":
            loader = TextLoader(file_path)
        elif extension == ".md":
            loader = UnstructuredMarkdownLoader(file_path)
        else:
            raise ValueError(f"Unsupported file type: {extension}")
        
        return loader.load()
    
    def _generate_document_id(self, file_path: str) -> str:
        """Generate unique document ID"""
        return hashlib.md5(file_path.encode()).hexdigest()
    
    async def retrieve_context(
        self,
        query: str,
        filters: Optional[Dict[str, Any]] = None,
        top_k: int = None
    ) -> List[Dict[str, Any]]:
        """
        Retrieve relevant context for a query
        
        Args:
            query: Search query
            filters: Metadata filters
            top_k: Number of results to return
        
        Returns:
            List of relevant documents with scores
        """
        try:
            top_k = top_k or settings.TOP_K_RESULTS
            
            # Perform similarity search
            if filters:
                results = self.vector_store.similarity_search_with_score(
                    query,
                    k=top_k,
                    filter=filters
                )
            else:
                results = self.vector_store.similarity_search_with_score(
                    query,
                    k=top_k
                )
            
            # Format results
            formatted_results = []
            for doc, score in results:
                formatted_results.append({
                    "content": doc.page_content,
                    "metadata": doc.metadata,
                    "score": float(score)
                })
            
            return formatted_results
            
        except Exception as e:
            logger.error(f"Error retrieving context: {e}")
            return []
    
    async def search_by_topic(
        self,
        topic: str,
        subject: Optional[str] = None,
        grade_level: Optional[str] = None,
        top_k: int = None
    ) -> List[Dict[str, Any]]:
        """
        Search for content by topic with optional filters
        
        Args:
            topic: Topic to search for
            subject: Subject filter
            grade_level: Grade level filter
            top_k: Number of results
        
        Returns:
            Relevant documents
        """
        filters = {}
        if subject:
            filters["subject"] = subject
        if grade_level:
            filters["grade_level"] = grade_level
        
        return await self.retrieve_context(topic, filters, top_k)
    
    async def get_document_summary(self, document_id: str) -> Optional[Dict[str, Any]]:
        """
        Get summary information about a processed document
        
        Args:
            document_id: Document ID
        
        Returns:
            Document summary
        """
        try:
            # Query for all chunks of the document
            results = self.vector_store.similarity_search(
                "",
                filter={"document_id": document_id},
                k=1000  # Get all chunks
            )
            
            if not results:
                return None
            
            # Calculate statistics
            total_chunks = len(results)
            total_chars = sum(len(doc.page_content) for doc in results)
            
            # Get metadata from first chunk
            metadata = results[0].metadata if results else {}
            
            return {
                "document_id": document_id,
                "total_chunks": total_chunks,
                "total_characters": total_chars,
                "metadata": metadata,
                "source_file": metadata.get("source_file", "Unknown")
            }
            
        except Exception as e:
            logger.error(f"Error getting document summary: {e}")
            return None
    
    async def delete_document(self, document_id: str) -> bool:
        """
        Delete a document from the vector store
        
        Args:
            document_id: Document ID to delete
        
        Returns:
            Success status
        """
        try:
            # Delete all chunks with this document ID
            self.vector_store.delete(
                filter={"document_id": document_id}
            )
            logger.info(f"Deleted document: {document_id}")
            return True
            
        except Exception as e:
            logger.error(f"Error deleting document: {e}")
            return False
    
    async def update_document_metadata(
        self,
        document_id: str,
        metadata: Dict[str, Any]
    ) -> bool:
        """
        Update metadata for a document
        
        Args:
            document_id: Document ID
            metadata: New metadata
        
        Returns:
            Success status
        """
        try:
            # This would require re-indexing in most vector stores
            # For now, log the request
            logger.info(f"Metadata update requested for document {document_id}")
            # Implementation depends on vector store capabilities
            return True
            
        except Exception as e:
            logger.error(f"Error updating metadata: {e}")
            return False