package quiz.config;

import quiz.core.*;
import java.util.List;
import java.util.Map;

/**
 * Manages dynamic configuration for the quiz engine
 */
public interface ConfigurationManager {
    
    /**
     * Create a new quiz configuration
     * @param config Configuration to save
     * @return Saved configuration with ID
     */
    QuizConfig saveConfiguration(QuizConfig config);
    
    /**
     * Get configuration by ID
     * @param configId Configuration identifier
     * @return Configuration or null if not found
     */
    QuizConfig getConfiguration(String configId);
    
    /**
     * Get configurations by subject
     * @param subject Subject filter
     * @return List of configurations for the subject
     */
    List<QuizConfig> getConfigurationsBySubject(String subject);
    
    /**
     * Get configurations by education level
     * @param level Education level filter
     * @return List of configurations for the level
     */
    List<QuizConfig> getConfigurationsByLevel(EducationLevel level);
    
    /**
     * Create configuration from template
     * @param templateId Template identifier
     * @param customizations Custom settings to override
     * @return Created configuration
     */
    QuizConfig createFromTemplate(String templateId, Map<String, Object> customizations);
    
    /**
     * Get predefined configuration templates
     * @return Map of template ID to template configuration
     */
    Map<String, QuizConfig> getConfigurationTemplates();
    
    /**
     * Register a new configuration template
     * @param templateId Template identifier
     * @param template Template configuration
     * @return true if registration was successful
     */
    boolean registerTemplate(String templateId, QuizConfig template);
    
    /**
     * Validate configuration
     * @param config Configuration to validate
     * @return Validation result
     */
    ConfigValidationResult validateConfiguration(QuizConfig config);
    
    /**
     * Get default configuration for subject and level
     * @param subject Subject
     * @param level Education level
     * @return Default configuration
     */
    QuizConfig getDefaultConfiguration(String subject, EducationLevel level);
    
    /**
     * Update an existing configuration
     * @param config Updated configuration
     * @return Updated configuration
     */
    QuizConfig updateConfiguration(QuizConfig config);
    
    /**
     * Delete a configuration
     * @param configId Configuration identifier
     * @return true if deletion was successful
     */
    boolean deleteConfiguration(String configId);
    
    /**
     * Get configuration statistics
     * @return Map of configuration usage statistics
     */
    Map<String, Object> getConfigurationStatistics();
}