# Quiz Engine - Dynamic RAG & LLM-Based Question Generation

A sophisticated quiz engine that leverages both **RAG (Retrieval-Augmented Generation)** and **LLM (Large Language Model)** technologies to dynamically generate educational quizzes from textbooks and other educational content.

## 🚀 Features

### Core Capabilities
- **Dual Generation Modes**: RAG-based (document-driven) and LLM-based (template-driven) question generation
- **Hybrid Approach**: Combines both RAG and LLM for comprehensive question coverage
- **Educational Content Support**: Optimized for school and college textbooks
- **Dynamic Configuration**: Flexible quiz settings for different educational levels
- **Multiple Question Types**: MCQ, True/False, Short Answer, Essay, Fill-in-the-Blank

### RAG Engine Features
- **Document Processing**: Intelligent chunking and indexing of educational content
- **Vector Search**: Semantic similarity search for relevant content retrieval
- **Context-Aware Generation**: Questions generated from specific textbook sections
- **Content Validation**: Ensures questions are grounded in source material

### LLM Integration
- **Template-Based Generation**: Configurable prompts for different question types
- **Difficulty Scaling**: Automatic adjustment based on educational level
- **Quality Validation**: Automated question quality assessment
- **Multi-Format Support**: Flexible output formats for various question types

### Dynamic Configuration
- **Education Level Targeting**: Elementary through Graduate level support
- **Subject Specialization**: Customizable for different academic subjects
- **Difficulty Progression**: Adaptive difficulty based on student level
- **Time Management**: Configurable time limits and pacing

## 🏗️ Architecture

```
Quiz Engine Architecture
├── Core Layer (quiz.core)
│   ├── Question & QuizConfig entities
│   ├── Enums (QuestionType, DifficultyLevel, etc.)
│   └── QuestionGenerator interface
├── RAG Engine (quiz.rag)
│   ├── Document processing & chunking
│   ├── Vector storage & retrieval
│   ├── Embedding services
│   └── Content-based question generation
├── LLM Integration (quiz.llm)
│   ├── LLM service abstraction
│   ├── Prompt template management
│   ├── Response parsing & validation
│   └── Dynamic question generation
├── Quiz Management (quiz.engine)
│   ├── Quiz creation & management
│   ├── Session handling
│   ├── Answer evaluation
│   └── Results processing
├── Configuration (quiz.config)
│   ├── Dynamic configuration management
│   ├── Template system
│   └── Validation framework
└── Demo & Examples (quiz.demo)
    ├── Usage demonstrations
    ├── Sample configurations
    └── Integration examples
```

## 🛠️ Technology Stack

- **Language**: Java (Core)
- **Architecture**: Modular, Interface-driven design
- **RAG Components**: Vector embeddings, Semantic search
- **LLM Integration**: Configurable LLM providers
- **Configuration**: Template-based with dynamic customization
- **Extensibility**: Plugin architecture for new question types

## 📚 Educational Content Support

### Supported Content Types
- **Textbooks**: PDF, DOCX, TXT formats
- **Educational Materials**: Lecture notes, study guides
- **Reference Materials**: Academic papers, encyclopedias
- **Structured Content**: Markdown, HTML documents

### Content Processing
- **Intelligent Chunking**: Semantic-aware text segmentation
- **Metadata Extraction**: Subject, topic, and level classification
- **Quality Filtering**: Content relevance and educational value assessment
- **Cross-Reference**: Linking related concepts across documents

## 🎯 Usage Examples

### Quick Start
```java
// Create a configuration
QuizConfig config = new QuizConfig.Builder()
    .withSubject("Mathematics")
    .withLevel(EducationLevel.HIGH_SCHOOL)
    .withQuestionCount(10)
    .withPreferredGenerator(GeneratorType.HYBRID)
    .build();

// Generate quiz (implementation would be injected)
QuizEngine engine = new QuizEngine(/* dependencies */);
Quiz quiz = engine.createQuiz(config);

// Start quiz session
QuizSession session = engine.startQuizSession(quiz.getId(), "student123");
```

### Using Templates
```java
// Use predefined template
QuizConfig config = ConfigurationTemplates.getTemplate("highschool_science_comprehensive");

// Customize template
Map<String, Object> customizations = new HashMap<>();
customizations.put("subject", "Chemistry");
customizations.put("topic", "Organic Chemistry");
QuizConfig customized = ConfigurationTemplates.customizeTemplate("book_based_quiz", customizations);
```

### RAG-Based Quiz from Textbook
```java
QuizConfig ragConfig = new QuizConfig.Builder()
    .withSubject("Biology")
    .withTopic("Cell Biology")
    .withLevel(EducationLevel.UNDERGRADUATE)
    .withPreferredGenerator(GeneratorType.RAG)
    .withContentSources(Arrays.asList("cell_biology_textbook.pdf"))
    .build();
```

## 🔧 Configuration Options

### Quiz Configuration
- **Subject & Topic**: Academic subject and specific topic focus
- **Education Level**: Elementary, Middle School, High School, Undergraduate, Graduate
- **Question Types**: Multiple choice, True/False, Short answer, Essay, Fill-in-blank
- **Difficulty**: Very Easy to Very Hard (1-5 scale)
- **Time Management**: Configurable time limits and pacing
- **Scoring**: Points-based, Percentage, Weighted, Pass/Fail

### Generation Settings
- **Generator Type**: RAG, LLM, or Hybrid approach
- **Content Sources**: Specific documents or content pools
- **Template Selection**: Question format templates
- **Quality Thresholds**: Minimum quality scores for generated questions

## 🎓 Educational Levels

- **Elementary (Grades 1-5)**: Basic concepts, simple language
- **Middle School (Grades 6-8)**: Intermediate concepts, structured thinking
- **High School (Grades 9-12)**: Advanced concepts, analytical thinking
- **Undergraduate (Years 1-4)**: Specialized knowledge, critical analysis
- **Graduate (Masters/PhD)**: Expert-level, research-oriented
- **Postgraduate**: Advanced research, specialized expertise

## 🔄 Dynamic Features

### Adaptive Difficulty
- Automatic difficulty adjustment based on education level
- Performance-based difficulty scaling
- Content complexity analysis

### Content Intelligence
- Semantic understanding of educational material
- Concept extraction and relationship mapping
- Quality assessment of source content

### Extensible Architecture
- Plugin system for new question types
- Custom generator implementations
- Configurable evaluation methods

## 🚀 Getting Started

1. **Setup Dependencies**: Configure LLM services and vector databases
2. **Load Content**: Process and index educational materials
3. **Configure Quiz**: Use templates or create custom configurations
4. **Generate Questions**: Run the quiz engine with your configuration
5. **Manage Sessions**: Handle user interactions and evaluate responses

## 📊 Performance & Analytics

- **Question Quality Metrics**: Validation scores and quality assessments
- **Generation Performance**: Speed and accuracy of question creation
- **User Analytics**: Performance tracking and learning insights
- **Content Utilization**: Usage statistics for source materials

## 🔮 Future Enhancements

- **Multi-language Support**: International education systems
- **Advanced NLP**: Improved content understanding and question generation
- **Learning Analytics**: Detailed student performance insights
- **Integration APIs**: LMS and educational platform connectivity
- **Mobile Support**: Responsive design for mobile devices

---

**Original Note**: This application was extended from an M.Sc(C.S) assessment project.
**Language**: Core Java with modern architectural patterns
