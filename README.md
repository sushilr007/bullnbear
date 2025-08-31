# Dynamic Quiz Engine

A scalable, microservices-based quiz engine that supports both RAG (Retrieval-Augmented Generation) and LLM-based quiz generation for educational content from school and college books.

## 🚀 Features

- **Multiple Quiz Generation Strategies**: RAG, LLM, and Hybrid approaches
- **Document Processing**: Support for PDF, DOCX, and TXT files
- **Vector Database**: ChromaDB for semantic search and retrieval
- **Multiple LLM Providers**: OpenAI GPT-4 and Anthropic Claude support
- **Real-time Quiz Sessions**: WebSocket-based quiz taking experience
- **User Management**: Authentication, authorization, and progress tracking
- **Scalable Architecture**: Microservices with Docker and Kubernetes support

## 🏗️ Architecture

The system consists of the following microservices:

- **API Gateway**: Single entry point for all operations
- **Quiz Generation Service**: Orchestrates RAG and LLM approaches
- **RAG Service**: Document processing and retrieval
- **LLM Service**: Direct LLM-based quiz generation
- **Content Management Service**: Book and content management
- **User Management Service**: Authentication and user profiles
- **Quiz Session Service**: Real-time quiz sessions

## 📋 Prerequisites

- Docker and Docker Compose
- Python 3.11+
- PostgreSQL 15+
- Redis 7+
- ChromaDB
- RabbitMQ (optional, for background tasks)

## 🛠️ Installation

### 1. Clone the Repository

```bash
git clone <repository-url>
cd quiz-engine
```

### 2. Environment Setup

Create a `.env` file in the root directory:

```bash
# Database
DATABASE_URL=postgresql://quiz_user:quiz_password@postgres:5432/quiz_engine

# Redis
REDIS_URL=redis://redis:6379

# ChromaDB
CHROMA_URL=http://chromadb:8000

# LLM API Keys
OPENAI_API_KEY=your_openai_api_key_here
ANTHROPIC_API_KEY=your_anthropic_api_key_here

# JWT Secret
JWT_SECRET_KEY=your_jwt_secret_key_here

# Service URLs
USER_MANAGEMENT_URL=http://user-management:8000
QUIZ_GENERATION_URL=http://quiz-generation:8000
RAG_SERVICE_URL=http://rag-service:8000
LLM_SERVICE_URL=http://llm-service:8000
CONTENT_MANAGEMENT_URL=http://content-management:8000
QUIZ_SESSION_URL=http://quiz-session:8000
```

### 3. Install Dependencies

```bash
pip install -r requirements.txt
```

### 4. Start Services

```bash
# Start all services with Docker Compose
docker-compose up -d

# Or start individual services
docker-compose up postgres redis chromadb rabbitmq -d
docker-compose up api-gateway -d
```

### 5. Initialize Database

```bash
# Create database tables
python -c "from shared.models.database import create_tables; create_tables()"
```

## 🚀 Quick Start

### 1. Start the API Gateway

```bash
cd api-gateway
uvicorn main:app --host 0.0.0.0 --port 8000 --reload
```

### 2. Register a User

```bash
curl -X POST "http://localhost:8000/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "teacher1",
    "email": "teacher@example.com",
    "password": "password123",
    "role": "teacher"
  }'
```

### 3. Login and Get Token

```bash
curl -X POST "http://localhost:8000/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "teacher@example.com",
    "password": "password123"
  }'
```

### 4. Create a Book

```bash
curl -X POST "http://localhost:8000/books" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Introduction to Computer Science",
    "author": "John Doe",
    "subject": "Computer Science",
    "grade_level": "College",
    "isbn": "978-0-123456-78-9",
    "description": "A comprehensive introduction to computer science concepts"
  }'
```

### 5. Upload Document Content

```bash
curl -X POST "http://localhost:8000/documents/upload" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "book_id": 1,
    "content": "Computer science is the study of computers and computational systems..."
  }'
```

### 6. Generate a Quiz

```bash
curl -X POST "http://localhost:8000/quizzes/generate" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "book_id": 1,
    "difficulty": "medium",
    "quiz_type": "rag",
    "num_questions": 10,
    "question_types": ["mcq", "true_false"],
    "time_limit": 30
  }'
```

## 📚 API Documentation

Once the services are running, you can access the interactive API documentation:

- **API Gateway**: http://localhost:8000/docs
- **RAG Service**: http://localhost:8001/docs
- **LLM Service**: http://localhost:8002/docs
- **Quiz Generation Service**: http://localhost:8003/docs

## 🔧 Configuration

### Quiz Generation Strategies

#### RAG-Based Generation
- Uses document chunks and vector similarity search
- Best for content-specific questions
- Requires document upload and processing

#### LLM-Based Generation
- Uses LLM knowledge directly
- Good for general knowledge questions
- Requires API keys for OpenAI or Anthropic

#### Hybrid Generation
- Combines RAG context with LLM creativity
- Best of both approaches
- More accurate and diverse questions

### Question Types

- **MCQ**: Multiple choice questions with 4 options
- **True/False**: Binary choice questions
- **Fill-in-Blank**: Text completion questions
- **Essay**: Open-ended questions

### Difficulty Levels

- **Easy**: Basic understanding questions
- **Medium**: Application and analysis questions
- **Hard**: Synthesis and evaluation questions

## 🧪 Testing

### Run Tests

```bash
# Run all tests
pytest

# Run specific service tests
pytest api-gateway/tests/
pytest rag-service/tests/
pytest llm-service/tests/
```

### Test Quiz Generation

```bash
# Test RAG generation
curl -X POST "http://localhost:8000/quizzes/generate" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{"book_id": 1, "quiz_type": "rag", "num_questions": 5}'

# Test LLM generation
curl -X POST "http://localhost:8000/quizzes/generate" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{"topic": "Python Programming", "quiz_type": "llm", "num_questions": 5}'

# Test Hybrid generation
curl -X POST "http://localhost:8000/quizzes/generate" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{"book_id": 1, "quiz_type": "hybrid", "num_questions": 5}'
```

## 📊 Monitoring

### Health Checks

```bash
# Check all services
curl http://localhost:8000/health
curl http://localhost:8001/health
curl http://localhost:8002/health
```

### Logs

```bash
# View service logs
docker-compose logs api-gateway
docker-compose logs rag-service
docker-compose logs llm-service
```

## 🔒 Security

- JWT-based authentication
- Role-based access control
- API rate limiting
- Input validation and sanitization
- Secure password hashing

## 📈 Scaling

### Horizontal Scaling

```bash
# Scale services
docker-compose up --scale quiz-generation=3
docker-compose up --scale rag-service=2
```

### Kubernetes Deployment

```bash
# Deploy to Kubernetes
kubectl apply -f k8s/
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:

- Create an issue on GitHub
- Check the documentation
- Review the API docs at `/docs` endpoints

## 🔮 Future Enhancements

- Adaptive difficulty adjustment
- Personalized learning paths
- Multi-language support
- Mobile app support
- LMS integration
- Advanced analytics
- Collaborative quizzes
- Offline mode support
