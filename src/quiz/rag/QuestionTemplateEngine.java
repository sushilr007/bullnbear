package quiz.rag;

import quiz.core.QuestionType;
import java.util.List;
import java.util.Map;

/**
 * Template engine for generating question stems and formats
 */
public interface QuestionTemplateEngine {
    
    /**
     * Generate a question stem from content and question type
     * @param content Source content
     * @param type Question type
     * @return Generated question stem
     */
    String generateQuestionStem(String content, QuestionType type);
    
    /**
     * Generate question stem using specific template
     * @param content Source content
     * @param templateId Template identifier
     * @return Generated question stem
     */
    String generateWithTemplate(String content, String templateId);
    
    /**
     * Get available templates for a question type
     * @param type Question type
     * @return List of template identifiers
     */
    List<String> getTemplatesForType(QuestionType type);
    
    /**
     * Register a new question template
     * @param templateId Template identifier
     * @param template Template string with placeholders
     * @param type Question type
     * @return true if registration was successful
     */
    boolean registerTemplate(String templateId, String template, QuestionType type);
    
    /**
     * Format question with options and metadata
     * @param questionText Question stem
     * @param options Answer options (for MCQ, etc.)
     * @param metadata Additional formatting metadata
     * @return Formatted question
     */
    String formatQuestion(String questionText, List<String> options, Map<String, Object> metadata);
    
    /**
     * Generate explanation template
     * @param content Source content
     * @param correctAnswer Correct answer
     * @return Generated explanation
     */
    String generateExplanation(String content, String correctAnswer);
    
    /**
     * Validate template syntax
     * @param template Template string
     * @return true if template is valid
     */
    boolean validateTemplate(String template);
    
    /**
     * Get all available templates
     * @return Map of template ID to template string
     */
    Map<String, String> getAllTemplates();
}