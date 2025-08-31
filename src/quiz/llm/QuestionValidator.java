package quiz.llm;

import quiz.core.Question;
import quiz.core.QuestionType;

/**
 * Validates generated questions for quality and completeness
 */
public interface QuestionValidator {
    
    /**
     * Validate if a question meets quality standards
     * @param question Question to validate
     * @return true if question is valid
     */
    boolean isValid(Question question);
    
    /**
     * Get detailed validation results
     * @param question Question to validate
     * @return Validation result with details
     */
    ValidationResult validate(Question question);
    
    /**
     * Validate question text quality
     * @param questionText Question text to validate
     * @param type Question type
     * @return true if text is acceptable
     */
    boolean isValidQuestionText(String questionText, QuestionType type);
    
    /**
     * Validate answer options for multiple choice questions
     * @param options List of answer options
     * @param correctAnswer Correct answer
     * @return true if options are valid
     */
    boolean areValidOptions(java.util.List<String> options, String correctAnswer);
    
    /**
     * Check if question difficulty matches expected level
     * @param question Question to check
     * @return true if difficulty is appropriate
     */
    boolean isDifficultyAppropriate(Question question);
    
    /**
     * Validate question for educational appropriateness
     * @param question Question to validate
     * @return true if educationally appropriate
     */
    boolean isEducationallyAppropriate(Question question);
    
    /**
     * Get validation score (0-100)
     * @param question Question to score
     * @return Quality score
     */
    double getQualityScore(Question question);
}