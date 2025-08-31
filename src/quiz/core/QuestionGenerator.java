package quiz.core;

import java.util.List;
import java.util.Map;

/**
 * Core interface for question generation strategies
 * Supports both RAG and LLM-based generation
 */
public interface QuestionGenerator {
    
    /**
     * Generate questions based on configuration
     * @param config Quiz configuration parameters
     * @return List of generated questions
     */
    List<Question> generateQuestions(QuizConfig config);
    
    /**
     * Generate questions from specific content
     * @param content Source content for question generation
     * @param config Quiz configuration
     * @return List of generated questions
     */
    List<Question> generateFromContent(String content, QuizConfig config);
    
    /**
     * Validate if generator can handle the given configuration
     * @param config Quiz configuration
     * @return true if generator supports this configuration
     */
    boolean supports(QuizConfig config);
    
    /**
     * Get generator type identifier
     * @return Generator type (RAG, LLM, HYBRID)
     */
    GeneratorType getType();
    
    /**
     * Get supported question types
     * @return List of supported question types
     */
    List<QuestionType> getSupportedTypes();
}