package quiz.llm;

import quiz.core.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * Manages prompt templates for different question types and educational levels
 */
public class PromptTemplateManager {
    
    private final Map<String, String> templates;
    private final Map<QuestionType, List<String>> typeTemplates;
    
    public PromptTemplateManager() {
        this.templates = new HashMap<>();
        this.typeTemplates = new HashMap<>();
        initializeDefaultTemplates();
    }
    
    /**
     * Build a prompt for generating a specific question type
     */
    public String buildPrompt(QuestionType type, QuizConfig config) {
        String template = getTemplateForType(type, config);
        return populateTemplate(template, config);
    }
    
    /**
     * Build a content-aware prompt that includes source material
     */
    public String buildContentAwarePrompt(QuestionType type, QuizConfig config, String content) {
        String template = getContentAwareTemplate(type, config);
        String basePrompt = populateTemplate(template, config);
        
        return basePrompt + "\n\nSource Content:\n" + content + 
               "\n\nGenerate a " + type.getDisplayName() + 
               " question based on the above content.";
    }
    
    /**
     * Get template for specific question type and configuration
     */
    private String getTemplateForType(QuestionType type, QuizConfig config) {
        String templateKey = buildTemplateKey(type, config);
        return templates.getOrDefault(templateKey, getDefaultTemplate(type));
    }
    
    /**
     * Get content-aware template
     */
    private String getContentAwareTemplate(QuestionType type, QuizConfig config) {
        String templateKey = "content_" + buildTemplateKey(type, config);
        return templates.getOrDefault(templateKey, getDefaultContentTemplate(type));
    }
    
    /**
     * Populate template with configuration values
     */
    private String populateTemplate(String template, QuizConfig config) {
        return template
                .replace("{SUBJECT}", config.getSubject() != null ? config.getSubject() : "")
                .replace("{TOPIC}", config.getTopic() != null ? config.getTopic() : "")
                .replace("{LEVEL}", config.getLevel() != null ? config.getLevel().getDisplayName() : "")
                .replace("{DIFFICULTY}", config.getDifficulty() != null ? 
                        config.getDifficulty().getDisplayName() : "Medium");
    }
    
    /**
     * Build template key based on type and config
     */
    private String buildTemplateKey(QuestionType type, QuizConfig config) {
        return type.getCode() + "_" + 
               (config.getLevel() != null ? config.getLevel().name() : "DEFAULT") + "_" +
               (config.getDifficulty() != null ? config.getDifficulty().name() : "MEDIUM");
    }
    
    /**
     * Initialize default templates for all question types
     */
    private void initializeDefaultTemplates() {
        // Multiple Choice Templates
        templates.put("mcq_DEFAULT_MEDIUM", 
            "Create a {DIFFICULTY} level multiple choice question about {SUBJECT}" +
            (", specifically about {TOPIC}" + "") + " suitable for {LEVEL} students.\n\n" +
            "Format:\n" +
            "Question: [Your question here]\n" +
            "A) [Option 1]\n" +
            "B) [Option 2]\n" +
            "C) [Option 3]\n" +
            "D) [Option 4]\n" +
            "Correct Answer: [A/B/C/D]\n" +
            "Explanation: [Brief explanation of the correct answer]");
        
        // True/False Templates
        templates.put("tf_DEFAULT_MEDIUM",
            "Create a {DIFFICULTY} level true/false question about {SUBJECT}" +
            " suitable for {LEVEL} students.\n\n" +
            "Format:\n" +
            "Question: [Your statement here]\n" +
            "Correct Answer: [True/False]\n" +
            "Explanation: [Brief explanation]");
        
        // Short Answer Templates
        templates.put("sa_DEFAULT_MEDIUM",
            "Create a {DIFFICULTY} level short answer question about {SUBJECT}" +
            " suitable for {LEVEL} students.\n\n" +
            "Format:\n" +
            "Question: [Your question here]\n" +
            "Answer: [Expected answer]\n" +
            "Acceptable Answers: [Alternative acceptable answers, comma-separated]\n" +
            "Explanation: [Brief explanation]");
        
        // Essay Templates
        templates.put("essay_DEFAULT_MEDIUM",
            "Create a {DIFFICULTY} level essay question about {SUBJECT}" +
            " suitable for {LEVEL} students.\n\n" +
            "Format:\n" +
            "Question: [Your essay prompt here]\n" +
            "Grading Guidelines: [Key points students should address]");
        
        // Fill in the Blank Templates
        templates.put("fitb_DEFAULT_MEDIUM",
            "Create a {DIFFICULTY} level fill-in-the-blank question about {SUBJECT}" +
            " suitable for {LEVEL} students.\n\n" +
            "Format:\n" +
            "Question: [Statement with _____ for the blank]\n" +
            "Answer: [Word or phrase that fills the blank]\n" +
            "Explanation: [Brief explanation]");
        
        // Content-aware templates
        templates.put("content_mcq_DEFAULT_MEDIUM",
            "Based on the provided content, create a {DIFFICULTY} level multiple choice question " +
            "about {SUBJECT} suitable for {LEVEL} students. The question should test understanding " +
            "of the key concepts in the content.");
        
        templates.put("content_tf_DEFAULT_MEDIUM",
            "Based on the provided content, create a {DIFFICULTY} level true/false question " +
            "that tests comprehension of the material.");
        
        templates.put("content_sa_DEFAULT_MEDIUM",
            "Based on the provided content, create a {DIFFICULTY} level short answer question " +
            "that requires students to demonstrate understanding of key concepts.");
        
        templates.put("content_fitb_DEFAULT_MEDIUM",
            "Based on the provided content, create a {DIFFICULTY} level fill-in-the-blank question " +
            "using an important sentence from the material.");
    }
    
    /**
     * Get default template for question type
     */
    private String getDefaultTemplate(QuestionType type) {
        switch (type) {
            case MULTIPLE_CHOICE:
                return templates.get("mcq_DEFAULT_MEDIUM");
            case TRUE_FALSE:
                return templates.get("tf_DEFAULT_MEDIUM");
            case SHORT_ANSWER:
                return templates.get("sa_DEFAULT_MEDIUM");
            case ESSAY:
                return templates.get("essay_DEFAULT_MEDIUM");
            case FILL_IN_THE_BLANK:
                return templates.get("fitb_DEFAULT_MEDIUM");
            default:
                return "Create a question about {SUBJECT} for {LEVEL} students.";
        }
    }
    
    /**
     * Get default content-aware template
     */
    private String getDefaultContentTemplate(QuestionType type) {
        switch (type) {
            case MULTIPLE_CHOICE:
                return templates.get("content_mcq_DEFAULT_MEDIUM");
            case TRUE_FALSE:
                return templates.get("content_tf_DEFAULT_MEDIUM");
            case SHORT_ANSWER:
                return templates.get("content_sa_DEFAULT_MEDIUM");
            case FILL_IN_THE_BLANK:
                return templates.get("content_fitb_DEFAULT_MEDIUM");
            default:
                return "Based on the provided content, create a question suitable for the specified educational level.";
        }
    }
    
    /**
     * Register a custom template
     */
    public void registerTemplate(String key, String template) {
        templates.put(key, template);
    }
    
    /**
     * Get all available templates
     */
    public Map<String, String> getAllTemplates() {
        return new HashMap<>(templates);
    }
    
    /**
     * Check if template exists
     */
    public boolean hasTemplate(String key) {
        return templates.containsKey(key);
    }
}