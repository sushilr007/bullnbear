package quiz.demo;

import quiz.core.*;
import quiz.engine.*;
import quiz.config.*;
import quiz.impl.*;
import quiz.rag.ContentChunk;
import java.util.*;

/**
 * Demonstration of the Quiz Engine capabilities
 */
public class QuizEngineDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Quiz Engine Demo ===\n");
        
        // Demonstrate configuration templates
        demonstrateConfigurationTemplates();
        
        // Demonstrate quiz creation scenarios
        demonstrateQuizCreationScenarios();
        
        // Demonstrate dynamic configuration
        demonstrateDynamicConfiguration();
        
        System.out.println("Demo completed successfully!");
    }
    
    /**
     * Demonstrate available configuration templates
     */
    private static void demonstrateConfigurationTemplates() {
        System.out.println("1. Available Configuration Templates:");
        System.out.println("====================================");
        
        Map<String, QuizConfig> templates = ConfigurationTemplates.getAllTemplates();
        
        for (Map.Entry<String, QuizConfig> entry : templates.entrySet()) {
            String templateId = entry.getKey();
            QuizConfig config = entry.getValue();
            
            System.out.println("Template: " + templateId);
            System.out.println("  Subject: " + config.getSubject());
            System.out.println("  Level: " + (config.getLevel() != null ? 
                              config.getLevel().getDisplayName() : "Any"));
            System.out.println("  Question Count: " + config.getQuestionCount());
            System.out.println("  Time Limit: " + config.getTimeLimit() + " minutes");
            System.out.println("  Generator: " + config.getPreferredGenerator());
            System.out.println("  Difficulty: " + config.getDifficulty());
            System.out.println();
        }
    }
    
    /**
     * Demonstrate different quiz creation scenarios
     */
    private static void demonstrateQuizCreationScenarios() {
        System.out.println("2. Quiz Creation Scenarios:");
        System.out.println("===========================");
        
        // Scenario 1: Elementary Math Quiz
        System.out.println("Scenario 1: Elementary Math Quiz");
        QuizConfig mathConfig = ConfigurationTemplates.getTemplate("elementary_math_basic");
        demonstrateQuizCreation(mathConfig, "Elementary Math");
        
        // Scenario 2: High School Science Quiz with RAG
        System.out.println("Scenario 2: High School Science Quiz (RAG-based)");
        QuizConfig scienceConfig = new QuizConfig.Builder()
                .withSubject("Biology")
                .withTopic("Cell Structure")
                .withLevel(EducationLevel.HIGH_SCHOOL)
                .withQuestionTypes(Arrays.asList(QuestionType.MULTIPLE_CHOICE, QuestionType.SHORT_ANSWER))
                .withDifficulty(DifficultyLevel.MEDIUM)
                .withQuestionCount(10)
                .withTimeLimit(25)
                .withPreferredGenerator(GeneratorType.RAG)
                .withContentSources(Arrays.asList("biology_textbook_ch3.pdf", "cell_biology_notes.txt"))
                .build();
        demonstrateQuizCreation(scienceConfig, "Biology - Cell Structure");
        
        // Scenario 3: College Literature Essay Quiz
        System.out.println("Scenario 3: College Literature Essay Quiz");
        QuizConfig literatureConfig = ConfigurationTemplates.getTemplate("college_literature_essay");
        literatureConfig.setTopic("Shakespeare's Hamlet");
        demonstrateQuizCreation(literatureConfig, "Hamlet Analysis");
        
        // Scenario 4: Hybrid Quiz
        System.out.println("Scenario 4: Hybrid Quiz (RAG + LLM)");
        QuizConfig hybridConfig = new QuizConfig.Builder()
                .withSubject("History")
                .withTopic("World War II")
                .withLevel(EducationLevel.UNDERGRADUATE)
                .withQuestionTypes(Arrays.asList(
                    QuestionType.MULTIPLE_CHOICE,
                    QuestionType.SHORT_ANSWER,
                    QuestionType.ESSAY))
                .withDifficulty(DifficultyLevel.HARD)
                .withQuestionCount(15)
                .withTimeLimit(60)
                .withPreferredGenerator(GeneratorType.HYBRID)
                .withScoringMethod(ScoringMethod.WEIGHTED)
                .build();
        demonstrateQuizCreation(hybridConfig, "World War II Comprehensive");
    }
    
    /**
     * Demonstrate dynamic configuration customization
     */
    private static void demonstrateDynamicConfiguration() {
        System.out.println("3. Dynamic Configuration:");
        System.out.println("=========================");
        
        // Start with a template
        String templateId = "book_based_quiz";
        System.out.println("Base Template: " + templateId);
        
        // Apply customizations
        Map<String, Object> customizations = new HashMap<>();
        customizations.put("subject", "Physics");
        customizations.put("topic", "Quantum Mechanics");
        customizations.put("level", EducationLevel.GRADUATE);
        customizations.put("difficulty", DifficultyLevel.VERY_HARD);
        customizations.put("questioncount", 20);
        customizations.put("timelimit", 90);
        
        QuizConfig customizedConfig = ConfigurationTemplates.customizeTemplate(templateId, customizations);
        
        System.out.println("Customized Configuration:");
        System.out.println("  Subject: " + customizedConfig.getSubject());
        System.out.println("  Topic: " + customizedConfig.getTopic());
        System.out.println("  Level: " + customizedConfig.getLevel().getDisplayName());
        System.out.println("  Difficulty: " + customizedConfig.getDifficulty().getDisplayName());
        System.out.println("  Question Count: " + customizedConfig.getQuestionCount());
        System.out.println("  Time Limit: " + customizedConfig.getTimeLimit() + " minutes");
        System.out.println();
        
        // Demonstrate template recommendations
        System.out.println("Template Recommendations for Physics Graduate Course:");
        List<String> recommendations = ConfigurationTemplates.getRecommendedTemplates(
            "Physics", EducationLevel.GRADUATE, GeneratorType.RAG);
        
        for (String recommendation : recommendations) {
            System.out.println("  - " + recommendation);
        }
        System.out.println();
    }
    
    /**
     * Demonstrate quiz creation process
     */
    private static void demonstrateQuizCreation(QuizConfig config, String quizName) {
        System.out.println("Creating Quiz: " + quizName);
        System.out.println("Configuration:");
        System.out.println("  Subject: " + config.getSubject());
        System.out.println("  Level: " + (config.getLevel() != null ? 
                          config.getLevel().getDisplayName() : "Not specified"));
        System.out.println("  Generator: " + config.getPreferredGenerator());
        System.out.println("  Question Count: " + config.getQuestionCount());
        System.out.println("  Time Limit: " + config.getTimeLimit() + " minutes");
        System.out.println("  Question Types: " + config.getQuestionTypes());
        
        // Simulate quiz creation process
        System.out.println("Status: Quiz creation simulated successfully");
        System.out.println("Quiz ID: QUIZ_" + System.currentTimeMillis());
        System.out.println();
    }
    
    /**
     * Demonstrate quiz session workflow
     */
    public static void demonstrateQuizSession() {
        System.out.println("4. Quiz Session Workflow:");
        System.out.println("=========================");
        
        // Create a sample quiz configuration
        QuizConfig config = new QuizConfig.Builder()
                .withSubject("Mathematics")
                .withTopic("Algebra")
                .withLevel(EducationLevel.HIGH_SCHOOL)
                .withQuestionTypes(Arrays.asList(QuestionType.MULTIPLE_CHOICE))
                .withQuestionCount(5)
                .withTimeLimit(10)
                .build();
        
        // Create sample questions
        List<Question> questions = createSampleQuestions();
        
        // Create quiz
        Quiz quiz = new Quiz.Builder(config)
                .withTitle("Algebra Basics Quiz")
                .withDescription("Test your understanding of basic algebra concepts")
                .withQuestions(questions)
                .build();
        
        System.out.println("Created Quiz: " + quiz.getTitle());
        System.out.println("Questions: " + quiz.getQuestionCount());
        System.out.println("Total Points: " + quiz.getTotalPoints());
        System.out.println("Estimated Duration: " + quiz.getStatistics().get("estimated_duration") + " minutes");
        
        // Simulate quiz session
        QuizSession session = new QuizSession(quiz, "user123");
        session.start();
        
        System.out.println("\nSession Started: " + session.getSessionId());
        System.out.println("Status: " + session.getStatus());
        System.out.println("Current Question: " + session.getCurrentQuestion().getText());
        
        // Simulate answering questions
        session.submitAnswer(questions.get(0).getId(), "Option A");
        System.out.println("Answer submitted for question 1");
        System.out.println("Progress: " + String.format("%.1f", session.getProgressPercentage()) + "%");
        
        session.complete();
        System.out.println("Session completed");
        System.out.println();
    }
    
    /**
     * Create sample questions for demonstration
     */
    private static List<Question> createSampleQuestions() {
        List<Question> questions = new ArrayList<>();
        
        // Sample MCQ
        questions.add(new Question.Builder("What is the value of x in the equation 2x + 5 = 13?", 
                                         QuestionType.MULTIPLE_CHOICE)
                .withOptions(Arrays.asList("4", "6", "8", "10"))
                .withCorrectAnswer("4")
                .withExplanation("2x + 5 = 13, so 2x = 8, therefore x = 4")
                .withDifficulty(DifficultyLevel.MEDIUM)
                .withPoints(2.0)
                .build());
        
        // Sample True/False
        questions.add(new Question.Builder("The quadratic formula can be used to solve any polynomial equation.", 
                                         QuestionType.TRUE_FALSE)
                .withOptions(Arrays.asList("True", "False"))
                .withCorrectAnswer("False")
                .withExplanation("The quadratic formula only works for quadratic equations (degree 2)")
                .withDifficulty(DifficultyLevel.MEDIUM)
                .withPoints(1.0)
                .build());
        
        // Sample Short Answer
        questions.add(new Question.Builder("What is the slope-intercept form of a linear equation?", 
                                         QuestionType.SHORT_ANSWER)
                .withCorrectAnswer("y = mx + b")
                .withAcceptableAnswers(Arrays.asList("y = mx + b", "y = mx + c", "y equals mx plus b"))
                .withExplanation("The slope-intercept form is y = mx + b, where m is slope and b is y-intercept")
                .withDifficulty(DifficultyLevel.EASY)
                .withPoints(2.0)
                .build());
        
        return questions;
    }
    
    /**
     * Demonstrate content processing
     */
    public static void demonstrateContentProcessing() {
        System.out.println("5. Content Processing Demo:");
        System.out.println("===========================");
        
        String sampleContent = 
            "Photosynthesis is the process by which plants convert light energy into chemical energy. " +
            "This process occurs in the chloroplasts of plant cells. " +
            "The overall equation for photosynthesis is: 6CO2 + 6H2O + light energy → C6H12O6 + 6O2. " +
            "Chlorophyll is the green pigment that captures light energy. " +
            "The process consists of two main stages: light-dependent reactions and light-independent reactions.";
        
        SimpleDocumentProcessor processor = new SimpleDocumentProcessor();
        List<ContentChunk> chunks = processor.processText(sampleContent, "Biology");
        
        System.out.println("Original Content Length: " + sampleContent.length() + " characters");
        System.out.println("Number of Chunks Created: " + chunks.size());
        
        for (int i = 0; i < chunks.size(); i++) {
            ContentChunk chunk = chunks.get(i);
            System.out.println("\nChunk " + (i + 1) + ":");
            System.out.println("  ID: " + chunk.getId());
            System.out.println("  Word Count: " + chunk.getMetadata().get("word_count"));
            System.out.println("  Content Preview: " + chunk.getPreview(100));
        }
        System.out.println();
    }
}