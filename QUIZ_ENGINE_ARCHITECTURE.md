# Quiz Engine Architecture

## Overview
A dynamic quiz engine that leverages both RAG (Retrieval-Augmented Generation) and LLM capabilities to generate questions from educational content like school and college books.

## Architecture Components

### 1. Core Architecture
```
┌─────────────────────────────────────────────────────────┐
│                   Quiz Engine                           │
├─────────────────────────────────────────────────────────┤
│                 Quiz Manager                            │
├─────────────────────────────────────────────────────────┤
│  Question Generator Layer                               │
│  ┌─────────────┐    ┌─────────────┐                    │
│  │ RAG Engine  │    │ LLM Engine  │                    │
│  └─────────────┘    └─────────────┘                    │
├─────────────────────────────────────────────────────────┤
│  Data Layer                                             │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐ │
│  │ Vector DB   │    │ Document    │    │ Config DB   │ │
│  │ (Embeddings)│    │ Store       │    │             │ │
│  └─────────────┘    └─────────────┘    └─────────────┘ │
└─────────────────────────────────────────────────────────┘
```

### 2. Component Details

#### RAG Engine
- **Document Processing**: Extract and chunk educational content
- **Embedding Generation**: Convert text to vector embeddings
- **Vector Search**: Retrieve relevant content for question generation
- **Context Assembly**: Combine retrieved chunks for coherent questions

#### LLM Engine
- **Dynamic Generation**: Create questions without specific source material
- **Template-based**: Use configurable templates for different question types
- **Difficulty Scaling**: Adjust complexity based on educational level
- **Multi-format Support**: MCQ, True/False, Short Answer, Essay

#### Quiz Manager
- **Session Management**: Handle quiz sessions and user progress
- **Configuration**: Dynamic quiz settings (difficulty, type, duration)
- **Scoring**: Automated evaluation and feedback
- **Analytics**: Performance tracking and insights

### 3. Key Features

#### Dynamic Configuration
- Subject-based question pools
- Difficulty levels (Elementary, High School, College, Graduate)
- Question types and formats
- Time limits and scoring methods

#### Educational Content Support
- Textbook processing and indexing
- Chapter-wise question generation
- Topic-specific quizzes
- Cross-subject integration

#### Scalability
- Modular architecture for easy extension
- Plugin system for new question types
- API-first design for integration
- Caching for performance optimization

### 4. Data Flow

1. **Content Ingestion**: Books/documents → Processing → Vector embeddings
2. **Question Generation**: 
   - RAG: Query → Retrieve → Generate
   - LLM: Template + Context → Generate
3. **Quiz Assembly**: Questions → Validation → Formatting → Delivery
4. **Evaluation**: Answers → Scoring → Feedback → Analytics

### 5. Technology Stack

- **Backend**: Java (Spring Boot)
- **Vector Database**: Chroma/Pinecone
- **LLM Integration**: OpenAI API/Local models
- **Document Processing**: Apache Tika
- **Database**: PostgreSQL
- **Caching**: Redis
- **API**: REST/GraphQL

### 6. Configuration Schema

```json
{
  "quizConfig": {
    "subject": "mathematics|science|history|literature",
    "level": "elementary|middle|high|college|graduate",
    "questionTypes": ["mcq", "true_false", "short_answer", "essay"],
    "source": "rag|llm|hybrid",
    "difficulty": 1-10,
    "duration": "minutes",
    "questionCount": "number"
  }
}
```

### 7. Extension Points

- Custom question generators
- New evaluation methods
- Additional content sources
- Integration with learning management systems
- Multi-language support