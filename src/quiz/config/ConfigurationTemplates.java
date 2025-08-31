package quiz.config;

import quiz.core.*;
import java.util.*;

/**
 * Provides predefined configuration templates for common quiz scenarios
 */
public class ConfigurationTemplates {
    
    private static final Map<String, QuizConfig> templates = new HashMap<>();
    
    static {
        initializeTemplates();
    }
    
    /**
     * Get all available templates
     */
    public static Map<String, QuizConfig> getAllTemplates() {
        return new HashMap<>(templates);
    }
    
    /**
     * Get template by ID
     */
    public static QuizConfig getTemplate(String templateId) {
        return templates.get(templateId);
    }
    
    /**
     * Check if template exists
     */
    public static boolean hasTemplate(String templateId) {
        return templates.containsKey(templateId);
    }
    
    /**
     * Register a new template
     */
    public static void registerTemplate(String templateId, QuizConfig template) {
        templates.put(templateId, template);
    }
    
    /**
     * Initialize default templates
     */
    private static void initializeTemplates() {
        
        // Elementary Math Quiz
        templates.put("elementary_math_basic", new QuizConfig.Builder()
                .withSubject("Mathematics")
                .withLevel(EducationLevel.ELEMENTARY)
                .withQuestionTypes(Arrays.asList(QuestionType.MULTIPLE_CHOICE, QuestionType.TRUE_FALSE))
                .withDifficulty(DifficultyLevel.EASY)
                .withQuestionCount(10)
                .withTimeLimit(15)
                .withPreferredGenerator(GeneratorType.LLM)
                .withScoringMethod(ScoringMethod.PERCENTAGE)
                .withPassingScore(70.0)
                .build());
        
        // High School Science Quiz
        templates.put("highschool_science_comprehensive", new QuizConfig.Builder()
                .withSubject("Science")
                .withLevel(EducationLevel.HIGH_SCHOOL)
                .withQuestionTypes(Arrays.asList(
                    QuestionType.MULTIPLE_CHOICE, 
                    QuestionType.SHORT_ANSWER, 
                    QuestionType.TRUE_FALSE))
                .withDifficulty(DifficultyLevel.MEDIUM)
                .withQuestionCount(20)
                .withTimeLimit(45)
                .withPreferredGenerator(GeneratorType.HYBRID)
                .withScoringMethod(ScoringMethod.WEIGHTED)
                .withPassingScore(75.0)
                .build());
        
        // College Literature Essay Quiz
        templates.put("college_literature_essay", new QuizConfig.Builder()
                .withSubject("Literature")
                .withLevel(EducationLevel.UNDERGRADUATE)
                .withQuestionTypes(Arrays.asList(QuestionType.ESSAY, QuestionType.SHORT_ANSWER))
                .withDifficulty(DifficultyLevel.HARD)
                .withQuestionCount(5)
                .withTimeLimit(90)
                .withPreferredGenerator(GeneratorType.RAG)
                .withScoringMethod(ScoringMethod.RUBRIC)
                .withPassingScore(80.0)
                .withPartialCredit(true)
                .build());
        
        // Graduate Research Quiz
        templates.put("graduate_research_advanced", new QuizConfig.Builder()
                .withSubject("Research Methods")
                .withLevel(EducationLevel.GRADUATE)
                .withQuestionTypes(Arrays.asList(
                    QuestionType.ESSAY, 
                    QuestionType.SHORT_ANSWER, 
                    QuestionType.MULTIPLE_CHOICE))
                .withDifficulty(DifficultyLevel.VERY_HARD)
                .withQuestionCount(8)
                .withTimeLimit(120)
                .withPreferredGenerator(GeneratorType.HYBRID)
                .withScoringMethod(ScoringMethod.RUBRIC)
                .withPassingScore(85.0)
                .withPartialCredit(true)
                .build());
        
        // Quick Assessment Template
        templates.put("quick_assessment", new QuizConfig.Builder()
                .withQuestionTypes(Arrays.asList(QuestionType.MULTIPLE_CHOICE, QuestionType.TRUE_FALSE))
                .withDifficulty(DifficultyLevel.MEDIUM)
                .withQuestionCount(5)
                .withTimeLimit(10)
                .withPreferredGenerator(GeneratorType.LLM)
                .withScoringMethod(ScoringMethod.PERCENTAGE)
                .withShuffleQuestions(true)
                .withShuffleOptions(true)
                .build());
        
        // Comprehensive Exam Template
        templates.put("comprehensive_exam", new QuizConfig.Builder()
                .withQuestionTypes(Arrays.asList(
                    QuestionType.MULTIPLE_CHOICE,
                    QuestionType.SHORT_ANSWER,
                    QuestionType.ESSAY,
                    QuestionType.FILL_IN_THE_BLANK))
                .withDifficulty(DifficultyLevel.HARD)
                .withQuestionCount(30)
                .withTimeLimit(180)
                .withPreferredGenerator(GeneratorType.HYBRID)
                .withScoringMethod(ScoringMethod.WEIGHTED)
                .withPartialCredit(true)
                .withPassingScore(80.0)
                .build());
        
        // RAG-focused Template for Book-based Quizzes
        templates.put("book_based_quiz", new QuizConfig.Builder()
                .withQuestionTypes(Arrays.asList(
                    QuestionType.MULTIPLE_CHOICE,
                    QuestionType.SHORT_ANSWER,
                    QuestionType.FILL_IN_THE_BLANK))
                .withDifficulty(DifficultyLevel.MEDIUM)
                .withQuestionCount(15)
                .withTimeLimit(30)
                .withPreferredGenerator(GeneratorType.RAG)
                .withScoringMethod(ScoringMethod.POINTS_BASED)
                .withPartialCredit(true)
                .withCustomSetting("require_source_content", true)
                .withCustomSetting("min_content_chunks", 3)
                .build());
        
        // Practice Quiz Template
        templates.put("practice_quiz", new QuizConfig.Builder()
                .withQuestionTypes(Arrays.asList(QuestionType.MULTIPLE_CHOICE))
                .withDifficulty(DifficultyLevel.EASY)
                .withQuestionCount(10)
                .withTimeLimit(0) // No time limit
                .withPreferredGenerator(GeneratorType.LLM)
                .withScoringMethod(ScoringMethod.PERCENTAGE)
                .withShuffleQuestions(true)
                .withShuffleOptions(true)
                .withCustomSetting("show_immediate_feedback", true)
                .build());
    }
    
    /**
     * Get templates by education level
     */
    public static List<QuizConfig> getTemplatesByLevel(EducationLevel level) {
        List<QuizConfig> levelTemplates = new ArrayList<>();
        for (QuizConfig template : templates.values()) {
            if (template.getLevel() == level || template.getLevel() == null) {
                levelTemplates.add(template);
            }
        }
        return levelTemplates;
    }
    
    /**
     * Get templates by subject
     */
    public static List<QuizConfig> getTemplatesBySubject(String subject) {
        List<QuizConfig> subjectTemplates = new ArrayList<>();
        for (QuizConfig template : templates.values()) {
            if (subject.equals(template.getSubject()) || template.getSubject() == null) {
                subjectTemplates.add(template);
            }
        }
        return subjectTemplates;
    }
    
    /**
     * Get templates by generator type
     */
    public static List<QuizConfig> getTemplatesByGenerator(GeneratorType generatorType) {
        List<QuizConfig> generatorTemplates = new ArrayList<>();
        for (QuizConfig template : templates.values()) {
            if (template.getPreferredGenerator() == generatorType) {
                generatorTemplates.add(template);
            }
        }
        return generatorTemplates;
    }
    
    /**
     * Create a customized configuration from template
     */
    public static QuizConfig customizeTemplate(String templateId, Map<String, Object> customizations) {
        QuizConfig template = getTemplate(templateId);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + templateId);
        }
        
        // Create a copy of the template
        QuizConfig.Builder builder = new QuizConfig.Builder()
                .withSubject(template.getSubject())
                .withTopic(template.getTopic())
                .withLevel(template.getLevel())
                .withQuestionTypes(template.getQuestionTypes())
                .withPreferredGenerator(template.getPreferredGenerator())
                .withDifficulty(template.getDifficulty())
                .withQuestionCount(template.getQuestionCount())
                .withTimeLimit(template.getTimeLimit())
                .withScoringMethod(template.getScoringMethod())
                .withPartialCredit(template.isAllowPartialCredit())
                .withPassingScore(template.getPassingScore())
                .withShuffleQuestions(template.isShuffleQuestions())
                .withShuffleOptions(template.isShuffleOptions());
        
        // Apply customizations
        for (Map.Entry<String, Object> entry : customizations.entrySet()) {
            applyCustomization(builder, entry.getKey(), entry.getValue());
        }
        
        return builder.build();
    }
    
    /**
     * Apply individual customization to builder
     */
    private static void applyCustomization(QuizConfig.Builder builder, String key, Object value) {
        switch (key.toLowerCase()) {
            case "subject":
                builder.withSubject((String) value);
                break;
            case "topic":
                builder.withTopic((String) value);
                break;
            case "level":
                if (value instanceof EducationLevel) {
                    builder.withLevel((EducationLevel) value);
                } else if (value instanceof String) {
                    builder.withLevel(EducationLevel.valueOf((String) value));
                }
                break;
            case "difficulty":
                if (value instanceof DifficultyLevel) {
                    builder.withDifficulty((DifficultyLevel) value);
                } else if (value instanceof String) {
                    builder.withDifficulty(DifficultyLevel.valueOf((String) value));
                }
                break;
            case "questioncount":
                builder.withQuestionCount((Integer) value);
                break;
            case "timelimit":
                builder.withTimeLimit((Integer) value);
                break;
            case "passingscore":
                builder.withPassingScore((Double) value);
                break;
            case "generator":
                if (value instanceof GeneratorType) {
                    builder.withPreferredGenerator((GeneratorType) value);
                } else if (value instanceof String) {
                    builder.withPreferredGenerator(GeneratorType.valueOf((String) value));
                }
                break;
            default:
                // Add as custom setting
                builder.withCustomSetting(key, value);
                break;
        }
    }
    
    /**
     * Get template recommendations based on criteria
     */
    public static List<String> getRecommendedTemplates(String subject, EducationLevel level, 
                                                      GeneratorType preferredGenerator) {
        List<String> recommendations = new ArrayList<>();
        
        for (Map.Entry<String, QuizConfig> entry : templates.entrySet()) {
            QuizConfig template = entry.getValue();
            int score = 0;
            
            // Score based on matching criteria
            if (subject != null && subject.equals(template.getSubject())) score += 3;
            if (level != null && level.equals(template.getLevel())) score += 2;
            if (preferredGenerator != null && preferredGenerator.equals(template.getPreferredGenerator())) score += 1;
            
            // Include templates with good scores
            if (score >= 2) {
                recommendations.add(entry.getKey());
            }
        }
        
        return recommendations;
    }
}