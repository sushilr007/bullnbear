package quiz.engine;

import quiz.core.*;
import quiz.rag.RAGQuestionGenerator;
import quiz.llm.LLMQuestionGenerator;
import quiz.config.ConfigurationManager;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Main quiz engine that orchestrates question generation and quiz management
 */
public class QuizEngine {
    
    private final Map<GeneratorType, QuestionGenerator> generators;
    private final QuizSessionManager sessionManager;
    private final QuizEvaluator evaluator;
    private final ConfigurationManager configManager;
    private final QuizRepository repository;
    
    public QuizEngine(RAGQuestionGenerator ragGenerator,
                     LLMQuestionGenerator llmGenerator,
                     QuizSessionManager sessionManager,
                     QuizEvaluator evaluator,
                     ConfigurationManager configManager,
                     QuizRepository repository) {
        
        this.generators = new HashMap<>();
        this.generators.put(GeneratorType.RAG, ragGenerator);
        this.generators.put(GeneratorType.LLM, llmGenerator);
        this.sessionManager = sessionManager;
        this.evaluator = evaluator;
        this.configManager = configManager;
        this.repository = repository;
    }
    
    /**
     * Create a new quiz based on configuration
     */
    public Quiz createQuiz(QuizConfig config) {
        try {
            // Validate configuration
            if (!config.isValid()) {
                throw new IllegalArgumentException("Invalid configuration: " + 
                                                 config.getValidationMessage());
            }
            
            // Generate questions using appropriate strategy
            List<Question> questions = generateQuestions(config);
            
            if (questions.isEmpty()) {
                throw new RuntimeException("No questions could be generated for the given configuration");
            }
            
            // Create quiz instance
            Quiz quiz = new Quiz.Builder(config)
                    .withQuestions(questions)
                    .build();
            
            // Apply post-processing
            quiz = applyPostProcessing(quiz, config);
            
            // Save quiz
            repository.save(quiz);
            
            return quiz;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to create quiz", e);
        }
    }
    
    /**
     * Start a quiz session
     */
    public QuizSession startQuizSession(String quizId, String userId) {
        try {
            Quiz quiz = repository.findById(quizId);
            if (quiz == null) {
                throw new IllegalArgumentException("Quiz not found: " + quizId);
            }
            
            QuizSession session = sessionManager.createSession(quiz, userId);
            return session;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to start quiz session", e);
        }
    }
    
    /**
     * Submit an answer to a quiz session
     */
    public AnswerResult submitAnswer(String sessionId, String questionId, String answer) {
        try {
            QuizSession session = sessionManager.getSession(sessionId);
            if (session == null) {
                throw new IllegalArgumentException("Session not found: " + sessionId);
            }
            
            AnswerResult result = evaluator.evaluateAnswer(session, questionId, answer);
            sessionManager.updateSession(session, questionId, result);
            
            return result;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to submit answer", e);
        }
    }
    
    /**
     * Complete a quiz session and get final results
     */
    public QuizResult completeQuizSession(String sessionId) {
        try {
            QuizSession session = sessionManager.getSession(sessionId);
            if (session == null) {
                throw new IllegalArgumentException("Session not found: " + sessionId);
            }
            
            QuizResult result = evaluator.calculateFinalScore(session);
            sessionManager.completeSession(session, result);
            
            return result;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to complete quiz session", e);
        }
    }
    
    /**
     * Generate questions using the appropriate strategy
     */
    private List<Question> generateQuestions(QuizConfig config) {
        List<Question> allQuestions = new ArrayList<>();
        
        // Determine generation strategy
        GeneratorType strategy = determineGenerationStrategy(config);
        
        switch (strategy) {
            case RAG:
                allQuestions = generateWithRAG(config);
                break;
            case LLM:
                allQuestions = generateWithLLM(config);
                break;
            case HYBRID:
                allQuestions = generateWithHybrid(config);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported generation strategy: " + strategy);
        }
        
        // Post-process questions
        return postProcessQuestions(allQuestions, config);
    }
    
    /**
     * Generate questions using RAG approach
     */
    private List<Question> generateWithRAG(QuizConfig config) {
        QuestionGenerator ragGenerator = generators.get(GeneratorType.RAG);
        if (ragGenerator == null) {
            throw new RuntimeException("RAG generator not available");
        }
        return ragGenerator.generateQuestions(config);
    }
    
    /**
     * Generate questions using LLM approach
     */
    private List<Question> generateWithLLM(QuizConfig config) {
        QuestionGenerator llmGenerator = generators.get(GeneratorType.LLM);
        if (llmGenerator == null) {
            throw new RuntimeException("LLM generator not available");
        }
        return llmGenerator.generateQuestions(config);
    }
    
    /**
     * Generate questions using hybrid approach
     */
    private List<Question> generateWithHybrid(QuizConfig config) {
        List<Question> questions = new ArrayList<>();
        
        // Split questions between RAG and LLM
        int ragQuestions = config.getQuestionCount() / 2;
        int llmQuestions = config.getQuestionCount() - ragQuestions;
        
        // Generate RAG questions
        if (ragQuestions > 0) {
            QuizConfig ragConfig = new QuizConfig.Builder()
                    .withSubject(config.getSubject())
                    .withTopic(config.getTopic())
                    .withLevel(config.getLevel())
                    .withQuestionTypes(config.getQuestionTypes())
                    .withDifficulty(config.getDifficulty())
                    .withQuestionCount(ragQuestions)
                    .build();
            
            List<Question> ragQuestionList = generateWithRAG(ragConfig);
            questions.addAll(ragQuestionList);
        }
        
        // Generate LLM questions
        if (llmQuestions > 0) {
            QuizConfig llmConfig = new QuizConfig.Builder()
                    .withSubject(config.getSubject())
                    .withTopic(config.getTopic())
                    .withLevel(config.getLevel())
                    .withQuestionTypes(config.getQuestionTypes())
                    .withDifficulty(config.getDifficulty())
                    .withQuestionCount(llmQuestions)
                    .build();
            
            List<Question> llmQuestionList = generateWithLLM(llmConfig);
            questions.addAll(llmQuestionList);
        }
        
        return questions;
    }
    
    /**
     * Determine the best generation strategy for the configuration
     */
    private GeneratorType determineGenerationStrategy(QuizConfig config) {
        if (config.getPreferredGenerator() != null) {
            return config.getPreferredGenerator();
        }
        
        // Auto-select based on configuration
        if (config.getContentSources() != null && !config.getContentSources().isEmpty()) {
            return GeneratorType.RAG;
        }
        
        // Default to hybrid for balanced approach
        return GeneratorType.HYBRID;
    }
    
    /**
     * Post-process generated questions
     */
    private List<Question> postProcessQuestions(List<Question> questions, QuizConfig config) {
        List<Question> processed = new ArrayList<>(questions);
        
        // Apply shuffling if configured
        if (config.isShuffleQuestions()) {
            Collections.shuffle(processed);
        }
        
        // Shuffle options for MCQ questions if configured
        if (config.isShuffleOptions()) {
            processed = processed.stream()
                               .map(this::shuffleQuestionOptions)
                               .collect(Collectors.toList());
        }
        
        // Ensure proper point distribution
        processed = distributePoints(processed, config);
        
        return processed;
    }
    
    /**
     * Apply post-processing to quiz
     */
    private Quiz applyPostProcessing(Quiz quiz, QuizConfig config) {
        // Set time limits
        if (config.getTimeLimit() > 0) {
            quiz.setTimeLimit(config.getTimeLimit());
        }
        
        // Apply any custom settings
        if (config.getCustomSettings() != null) {
            quiz.getMetadata().putAll(config.getCustomSettings());
        }
        
        return quiz;
    }
    
    /**
     * Shuffle options for a question if applicable
     */
    private Question shuffleQuestionOptions(Question question) {
        if (question.getType() == QuestionType.MULTIPLE_CHOICE && 
            question.getOptions() != null) {
            
            List<String> options = new ArrayList<>(question.getOptions());
            Collections.shuffle(options);
            question.setOptions(options);
        }
        return question;
    }
    
    /**
     * Distribute points across questions
     */
    private List<Question> distributePoints(List<Question> questions, QuizConfig config) {
        if (questions.isEmpty()) return questions;
        
        double totalPoints = 100.0; // Default total points
        double pointsPerQuestion = totalPoints / questions.size();
        
        for (Question question : questions) {
            if (question.getPoints() == 0) {
                // Assign points based on question type and difficulty
                double points = calculateQuestionPoints(question, pointsPerQuestion);
                question.setPoints(points);
            }
        }
        
        return questions;
    }
    
    /**
     * Calculate points for a question based on type and difficulty
     */
    private double calculateQuestionPoints(Question question, double basePoints) {
        double multiplier = 1.0;
        
        // Adjust based on question type
        switch (question.getType()) {
            case ESSAY:
                multiplier = 2.0;
                break;
            case SHORT_ANSWER:
                multiplier = 1.5;
                break;
            case FILL_IN_THE_BLANK:
                multiplier = 1.2;
                break;
            case MULTIPLE_CHOICE:
            case TRUE_FALSE:
                multiplier = 1.0;
                break;
        }
        
        // Adjust based on difficulty
        if (question.getDifficulty() != null) {
            multiplier *= (1.0 + (question.getDifficulty().getLevel() - 3) * 0.1);
        }
        
        return basePoints * multiplier;
    }
    
    // Getter methods for components
    public QuizSessionManager getSessionManager() { return sessionManager; }
    public QuizEvaluator getEvaluator() { return evaluator; }
    public ConfigurationManager getConfigManager() { return configManager; }
    public QuizRepository getRepository() { return repository; }
    
    /**
     * Get available generators
     */
    public Map<GeneratorType, QuestionGenerator> getGenerators() {
        return new HashMap<>(generators);
    }
    
    /**
     * Add a new question generator
     */
    public void addGenerator(GeneratorType type, QuestionGenerator generator) {
        generators.put(type, generator);
    }
    
    /**
     * Get engine statistics
     */
    public Map<String, Object> getEngineStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("available_generators", generators.keySet());
        stats.put("total_quizzes", repository.getTotalCount());
        stats.put("active_sessions", sessionManager.getActiveSessionCount());
        return stats;
    }
}