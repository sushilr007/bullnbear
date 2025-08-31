# 🎓 Intelligent Quiz Engine

A dynamic, AI-powered quiz generation system that combines RAG (Retrieval-Augmented Generation) and LLM capabilities to create educational quizzes from textbooks and educational content.

## 🌟 Features

### Core Capabilities
- **Dual Mode Operation**: RAG-based (from uploaded content) and LLM-based (from AI knowledge)
- **Multiple Question Types**: MCQ, True/False, Fill-in-the-blanks, Short Answer, Essay
- **Adaptive Learning**: Adjusts difficulty based on user performance
- **Content Processing**: Supports PDF, DOCX, TXT, and Markdown files
- **Smart Evaluation**: AI-powered answer validation with detailed feedback
- **Real-time Analytics**: Track performance and learning progress

### Technical Features
- **Microservices Architecture**: Scalable and maintainable design
- **Vector Database Integration**: Efficient content retrieval using ChromaDB/Pinecone
- **Async Processing**: Celery-based task queue for heavy operations
- **RESTful API**: Well-documented API with OpenAPI/Swagger
- **Modern UI**: React-based responsive interface with Material-UI

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                           Frontend (React)                           │
└─────────────────────────────────────────────────────────────────────┘
                                    │
┌─────────────────────────────────────────────────────────────────────┐
│                         API Gateway (FastAPI)                        │
└─────────────────────────────────────────────────────────────────────┘
                                    │
        ┌───────────────┬───────────┴───────────┬───────────────┐
        │               │                       │               │
┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│ Quiz Engine  │ │ RAG Service  │ │ LLM Service  │ │   Celery     │
└──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘
        │               │               │               │
┌──────────────────────────────────────────────────────────────────┐
│                    Data Layer (PostgreSQL, MongoDB)              │
└──────────────────────────────────────────────────────────────────┘
┌──────────────────────────────────────────────────────────────────┐
│              Vector Store (ChromaDB/Pinecone/Weaviate)           │
└──────────────────────────────────────────────────────────────────┘
```

## 🚀 Quick Start

### Prerequisites
- Docker and Docker Compose
- Python 3.9+
- Node.js 16+
- OpenAI API Key (or other LLM provider)

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/yourusername/quiz-engine.git
cd quiz-engine
```

2. **Set up environment variables**
```bash
cp .env.example .env
# Edit .env and add your API keys
```

3. **Start with Docker Compose**
```bash
docker-compose up -d
```

4. **Access the application**
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- API Documentation: http://localhost:8080/docs
- Celery Flower: http://localhost:5555

### Manual Setup (Development)

#### Backend Setup
```bash
cd backend
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate
pip install -r requirements.txt
uvicorn app.main:app --reload
```

#### Frontend Setup
```bash
cd frontend
npm install
npm start
```

## 📚 Usage Guide

### 1. Upload Educational Content
- Navigate to "Upload Content"
- Select PDF, DOCX, or TXT files
- Add metadata (subject, grade level, topics)
- Content is automatically processed and indexed

### 2. Generate Quizzes

#### RAG-Based Generation
- Uses uploaded educational content
- Ensures questions are directly from study material
- Perfect for exam preparation

#### LLM-Based Generation
- Uses AI knowledge base
- Good for general topics
- Provides broader coverage

#### Hybrid Mode
- Combines both approaches
- Best balance of accuracy and coverage

### 3. Take Quizzes
- Timer-based or untimed modes
- Get hints when stuck
- Immediate feedback on answers
- Detailed explanations

### 4. Track Progress
- View analytics dashboard
- Monitor performance trends
- Identify weak areas
- Get personalized recommendations

## 🔧 Configuration

### Environment Variables

```env
# API Keys
OPENAI_API_KEY=your_openai_key
ANTHROPIC_API_KEY=your_anthropic_key

# Database
DATABASE_URL=postgresql://user:pass@localhost/quizdb
MONGODB_URL=mongodb://localhost:27017/quizdb
REDIS_URL=redis://localhost:6379/0

# Vector Store
VECTOR_DB_TYPE=chromadb
CHROMADB_HOST=localhost
CHROMADB_PORT=8000

# Application
SECRET_KEY=your-secret-key
DEBUG=false
```

### Quiz Configuration

```python
# Default settings in config.py
DEFAULT_QUESTIONS_PER_QUIZ = 10
MAX_QUESTIONS_PER_QUIZ = 50
QUIZ_TIME_LIMIT_MINUTES = 30
CHUNK_SIZE = 1000
CHUNK_OVERLAP = 200
TOP_K_RESULTS = 5
```

## 📊 API Documentation

### Key Endpoints

#### Quiz Generation
```http
POST /api/v1/quiz/generate
{
  "mode": "rag_based",
  "topic": "Algebra",
  "num_questions": 10,
  "difficulty": "medium",
  "question_types": ["multiple_choice", "true_false"]
}
```

#### Content Upload
```http
POST /api/v1/rag/upload
Content-Type: multipart/form-data

file: algebra_textbook.pdf
subject: Mathematics
grade_level: High School
```

#### Quiz Submission
```http
POST /api/v1/quiz/submit
{
  "quiz_id": "quiz_123",
  "answers": [
    {"question_index": 0, "answer": "A"},
    {"question_index": 1, "answer": "True"}
  ]
}
```

## 🧪 Testing

```bash
# Backend tests
cd backend
pytest tests/ -v --cov=app

# Frontend tests
cd frontend
npm test
```

## 📈 Performance Optimization

### Caching Strategy
- Redis for session management
- Query result caching
- Embedding cache for frequent searches

### Scaling Options
- Horizontal scaling with Kubernetes
- Load balancing with Nginx
- Database sharding for large deployments

## 🔒 Security

- JWT-based authentication
- Role-based access control (Student, Teacher, Admin)
- Input validation and sanitization
- Rate limiting on API endpoints
- Encrypted data storage

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- OpenAI for GPT models
- LangChain for RAG framework
- ChromaDB for vector storage
- FastAPI for backend framework
- React and Material-UI for frontend

## 📞 Support

For issues and questions:
- GitHub Issues: [Create an issue](https://github.com/yourusername/quiz-engine/issues)
- Email: support@quizengine.com
- Documentation: [Full Docs](https://docs.quizengine.com)

## 🚦 Status

- Backend API: ✅ Complete
- RAG System: ✅ Complete
- LLM Integration: ✅ Complete
- Frontend UI: ✅ Basic Implementation
- Documentation: ✅ Complete
- Testing: 🔄 In Progress
- Production Deployment: 📅 Planned

---

Built with ❤️ for educators and learners everywhere