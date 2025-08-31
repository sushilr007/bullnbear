#!/usr/bin/env python3
"""
Example script demonstrating quiz generation using the Dynamic Quiz Engine.
This script shows how to use RAG, LLM, and Hybrid approaches for quiz generation.
"""

import asyncio
import httpx
import json
from typing import Dict, Any

# Configuration
API_BASE_URL = "http://localhost:8000"
API_KEY = "your_api_key_here"  # Replace with actual API key

class QuizEngineExample:
    """Example class demonstrating quiz engine usage"""
    
    def __init__(self, base_url: str, api_key: str):
        self.base_url = base_url
        self.headers = {
            "Authorization": f"Bearer {api_key}",
            "Content-Type": "application/json"
        }
    
    async def register_user(self, username: str, email: str, password: str, role: str = "teacher") -> Dict[str, Any]:
        """Register a new user"""
        async with httpx.AsyncClient() as client:
            response = await client.post(
                f"{self.base_url}/auth/register",
                json={
                    "username": username,
                    "email": email,
                    "password": password,
                    "role": role
                }
            )
            return response.json()
    
    async def login_user(self, email: str, password: str) -> Dict[str, Any]:
        """Login user and get token"""
        async with httpx.AsyncClient() as client:
            response = await client.post(
                f"{self.base_url}/auth/login",
                json={
                    "email": email,
                    "password": password
                }
            )
            return response.json()
    
    async def create_book(self, book_data: Dict[str, Any]) -> Dict[str, Any]:
        """Create a new book"""
        async with httpx.AsyncClient() as client:
            response = await client.post(
                f"{self.base_url}/books",
                json=book_data,
                headers=self.headers
            )
            return response.json()
    
    async def upload_document_content(self, book_id: int, content: str) -> Dict[str, Any]:
        """Upload document content for RAG processing"""
        async with httpx.AsyncClient() as client:
            response = await client.post(
                f"{self.base_url}/documents/upload",
                json={
                    "book_id": book_id,
                    "content": content
                },
                headers=self.headers
            )
            return response.json()
    
    async def generate_rag_quiz(self, book_id: int, num_questions: int = 10) -> Dict[str, Any]:
        """Generate quiz using RAG approach"""
        async with httpx.AsyncClient() as client:
            response = await client.post(
                f"{self.base_url}/quizzes/generate",
                json={
                    "book_id": book_id,
                    "quiz_type": "rag",
                    "num_questions": num_questions,
                    "difficulty": "medium",
                    "question_types": ["mcq", "true_false"],
                    "time_limit": 30
                },
                headers=self.headers
            )
            return response.json()
    
    async def generate_llm_quiz(self, topic: str, num_questions: int = 10) -> Dict[str, Any]:
        """Generate quiz using LLM approach"""
        async with httpx.AsyncClient() as client:
            response = await client.post(
                f"{self.base_url}/quizzes/generate",
                json={
                    "topic": topic,
                    "quiz_type": "llm",
                    "num_questions": num_questions,
                    "difficulty": "medium",
                    "question_types": ["mcq", "true_false"],
                    "time_limit": 30
                },
                headers=self.headers
            )
            return response.json()
    
    async def generate_hybrid_quiz(self, book_id: int, topic: str, num_questions: int = 10) -> Dict[str, Any]:
        """Generate quiz using hybrid approach"""
        async with httpx.AsyncClient() as client:
            response = await client.post(
                f"{self.base_url}/quizzes/generate",
                json={
                    "book_id": book_id,
                    "topic": topic,
                    "quiz_type": "hybrid",
                    "num_questions": num_questions,
                    "difficulty": "medium",
                    "question_types": ["mcq", "true_false"],
                    "time_limit": 30
                },
                headers=self.headers
            )
            return response.json()
    
    async def get_quiz_questions(self, quiz_id: int) -> Dict[str, Any]:
        """Get questions for a specific quiz"""
        async with httpx.AsyncClient() as client:
            response = await client.get(
                f"{self.base_url}/quizzes/{quiz_id}/questions",
                headers=self.headers
            )
            return response.json()

async def main():
    """Main function demonstrating quiz generation"""
    
    # Initialize quiz engine
    quiz_engine = QuizEngineExample(API_BASE_URL, API_KEY)
    
    print("🚀 Dynamic Quiz Engine Example")
    print("=" * 50)
    
    try:
        # Step 1: Register and login user
        print("\n1. Registering user...")
        user_data = await quiz_engine.register_user(
            username="demo_teacher",
            email="demo@example.com",
            password="password123",
            role="teacher"
        )
        print(f"✅ User registered: {user_data['username']}")
        
        # Step 2: Login to get token
        print("\n2. Logging in...")
        login_data = await quiz_engine.login_user(
            email="demo@example.com",
            password="password123"
        )
        print(f"✅ Login successful: {login_data['access_token'][:20]}...")
        
        # Update headers with new token
        quiz_engine.headers["Authorization"] = f"Bearer {login_data['access_token']}"
        
        # Step 3: Create a book
        print("\n3. Creating a book...")
        book_data = await quiz_engine.create_book({
            "title": "Introduction to Python Programming",
            "author": "John Smith",
            "subject": "Computer Science",
            "grade_level": "College",
            "isbn": "978-0-123456-78-9",
            "description": "A comprehensive introduction to Python programming language"
        })
        book_id = book_data['id']
        print(f"✅ Book created: {book_data['title']} (ID: {book_id})")
        
        # Step 4: Upload document content
        print("\n4. Uploading document content...")
        content = """
        Python is a high-level, interpreted programming language known for its simplicity and readability.
        It was created by Guido van Rossum and first released in 1991. Python supports multiple programming paradigms,
        including procedural, object-oriented, and functional programming.
        
        Key features of Python include:
        - Easy to learn and use
        - Extensive standard library
        - Cross-platform compatibility
        - Strong community support
        
        Python is widely used in:
        - Web development (Django, Flask)
        - Data science and machine learning
        - Scientific computing
        - Automation and scripting
        - Game development
        
        Variables in Python are dynamically typed, meaning you don't need to declare their type explicitly.
        For example: x = 10 creates an integer variable, while y = "Hello" creates a string variable.
        
        Python uses indentation to define code blocks, which makes the code more readable.
        Functions are defined using the 'def' keyword, and classes are defined using the 'class' keyword.
        """
        
        upload_result = await quiz_engine.upload_document_content(book_id, content)
        print(f"✅ Content uploaded: {upload_result['chunks_created']} chunks created")
        
        # Step 5: Generate RAG-based quiz
        print("\n5. Generating RAG-based quiz...")
        rag_quiz = await quiz_engine.generate_rag_quiz(book_id, num_questions=5)
        print(f"✅ RAG quiz generated: {rag_quiz['title']} (ID: {rag_quiz['quiz_id']})")
        print(f"   Questions: {rag_quiz['total_questions']}")
        print(f"   Estimated time: {rag_quiz['estimated_time']} minutes")
        
        # Step 6: Generate LLM-based quiz
        print("\n6. Generating LLM-based quiz...")
        llm_quiz = await quiz_engine.generate_llm_quiz("Python Programming", num_questions=5)
        print(f"✅ LLM quiz generated: {llm_quiz['title']} (ID: {llm_quiz['quiz_id']})")
        print(f"   Questions: {llm_quiz['total_questions']}")
        print(f"   Estimated time: {llm_quiz['estimated_time']} minutes")
        
        # Step 7: Generate Hybrid quiz
        print("\n7. Generating Hybrid quiz...")
        hybrid_quiz = await quiz_engine.generate_hybrid_quiz(book_id, "Python Programming", num_questions=5)
        print(f"✅ Hybrid quiz generated: {hybrid_quiz['title']} (ID: {hybrid_quiz['quiz_id']})")
        print(f"   Questions: {hybrid_quiz['total_questions']}")
        print(f"   Estimated time: {hybrid_quiz['estimated_time']} minutes")
        
        # Step 8: Get quiz questions (example with RAG quiz)
        print("\n8. Retrieving quiz questions...")
        questions = await quiz_engine.get_quiz_questions(rag_quiz['quiz_id'])
        print(f"✅ Retrieved {questions['total']} questions")
        
        # Display sample questions
        print("\n📝 Sample Questions:")
        for i, q_data in enumerate(questions['questions'][:2], 1):
            question = q_data['question']
            answers = q_data['answers']
            print(f"\nQuestion {i}: {question['question_text']}")
            print(f"Type: {question['question_type']}")
            print(f"Difficulty: {question['difficulty']}")
            print("Answers:")
            for j, answer in enumerate(answers, 1):
                correct = "✓" if answer['is_correct'] else "✗"
                print(f"  {j}. {answer['answer_text']} {correct}")
        
        print("\n🎉 Quiz generation example completed successfully!")
        
    except Exception as e:
        print(f"❌ Error: {e}")
        print("Make sure all services are running and API keys are configured correctly.")

if __name__ == "__main__":
    # Run the example
    asyncio.run(main())