package quiz.llm;

import quiz.core.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * LLM-based question generator that creates questions using language models
 * without requiring specific source documents
 */
public class LLMQuestionGenerator implements QuestionGenerator {
    
    private final LLMService llmService;
    private final PromptTemplateManager promptManager;
    private final QuestionValidator questionValidator;
    private final Map<String, Object> defaultSettings;
    
    public LLMQuestionGenerator(LLMService llmService,
                               PromptTemplateManager promptManager,
                               QuestionValidator questionValidator) {
        this.llmService = llmService;
        this.promptManager = promptManager;
        this.questionValidator = questionValidator;
        this.defaultSettings = new HashMap<>();
        initializeDefaultSettings();
    }
    
    @Override
    public List<Question> generateQuestions(QuizConfig config) {
        List<Question> questions = new ArrayList<>();
        
        try {
            // Validate configuration
            if (!config.isValid()) {
                throw new IllegalArgumentException("Invalid quiz configuration: " + 
                                                 config.getValidationMessage());
            }
            
            // Determine question types to generate
            List<QuestionType> typesToGenerate = determineQuestionTypes(config);
            
            // Generate questions for each type
            for (QuestionType type : typesToGenerate) {
                List<Question> typeQuestions = generateQuestionsOfType(type, config);
                questions.addAll(typeQuestions);
                
                if (questions.size() >= config.getQuestionCount()) {
                    break;
                }
            }
            
            // Trim to exact count and validate
            questions = questions.stream()
                                .limit(config.getQuestionCount())
                                .filter(q -> questionValidator.isValid(q))
                                .collect(Collectors.toList());
            
            // Fill any gaps if validation removed questions
            while (questions.size() < config.getQuestionCount()) {
                Question additionalQuestion = generateSingleQuestion(config);
                if (additionalQuestion != null && questionValidator.isValid(additionalQuestion)) {
                    questions.add(additionalQuestion);
                }
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate LLM questions", e);
        }
        
        return questions;
    }
    
    @Override
    public List<Question> generateFromContent(String content, QuizConfig config) {
        List<Question> questions = new ArrayList<>();
        
        try {
            // Create enhanced config with content context
            QuizConfig enhancedConfig = createContentEnhancedConfig(config, content);
            
            // Generate questions with content awareness
            List<QuestionType> typesToGenerate = determineQuestionTypes(enhancedConfig);
            
            for (QuestionType type : typesToGenerate) {
                List<Question> typeQuestions = generateQuestionsFromContentOfType(
                    content, type, enhancedConfig);
                questions.addAll(typeQuestions);
                
                if (questions.size() >= config.getQuestionCount()) {
                    break;
                }
            }
            
            return questions.stream()
                           .limit(config.getQuestionCount())
                           .collect(Collectors.toList());
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate questions from content", e);
        }
    }
    
    @Override
    public boolean supports(QuizConfig config) {
        return config.getPreferredGenerator() == GeneratorType.LLM ||
               config.getPreferredGenerator() == GeneratorType.HYBRID;
    }
    
    @Override
    public GeneratorType getType() {
        return GeneratorType.LLM;
    }
    
    @Override
    public List<QuestionType> getSupportedTypes() {
        return Arrays.asList(
            QuestionType.MULTIPLE_CHOICE,
            QuestionType.TRUE_FALSE,
            QuestionType.SHORT_ANSWER,
            QuestionType.ESSAY,
            QuestionType.FILL_IN_THE_BLANK
        );
    }
    
    /**
     * Generate questions of a specific type
     */
    private List<Question> generateQuestionsOfType(QuestionType type, QuizConfig config) {
        List<Question> questions = new ArrayList<>();
        int questionsPerType = Math.max(1, config.getQuestionCount() / 
                                      determineQuestionTypes(config).size());
        
        for (int i = 0; i < questionsPerType; i++) {
            Question question = generateSingleQuestionOfType(type, config);
            if (question != null) {
                questions.add(question);
            }
        }
        
        return questions;
    }
    
    /**
     * Generate a single question of specific type
     */
    private Question generateSingleQuestionOfType(QuestionType type, QuizConfig config) {
        try {
            // Build prompt for the question type
            String prompt = promptManager.buildPrompt(type, config);
            
            // Generate question using LLM
            LLMResponse response = llmService.generateText(prompt, buildLLMSettings(config));
            
            // Parse and validate response
            Question question = parseQuestionFromResponse(response, type, config);
            
            if (question != null && questionValidator.isValid(question)) {
                return question;
            }
            
        } catch (Exception e) {
            System.err.println("Failed to generate " + type + " question: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Generate questions from specific content
     */
    private List<Question> generateQuestionsFromContentOfType(String content, 
                                                             QuestionType type, 
                                                             QuizConfig config) {
        List<Question> questions = new ArrayList<>();
        int questionsPerType = Math.max(1, config.getQuestionCount() / 
                                      determineQuestionTypes(config).size());
        
        for (int i = 0; i < questionsPerType; i++) {
            try {
                // Build content-aware prompt
                String prompt = promptManager.buildContentAwarePrompt(type, config, content);
                
                // Generate question
                LLMResponse response = llmService.generateText(prompt, buildLLMSettings(config));
                
                // Parse and validate
                Question question = parseQuestionFromResponse(response, type, config);
                if (question != null && questionValidator.isValid(question)) {
                    question.setSourceContent(content);
                    questions.add(question);
                }
                
            } catch (Exception e) {
                System.err.println("Failed to generate content-based " + type + 
                                 " question: " + e.getMessage());
            }
        }
        
        return questions;
    }
    
    /**
     * Generate a single question with fallback type selection
     */
    private Question generateSingleQuestion(QuizConfig config) {
        List<QuestionType> supportedTypes = getSupportedTypes();
        Collections.shuffle(supportedTypes);
        
        for (QuestionType type : supportedTypes) {
            Question question = generateSingleQuestionOfType(type, config);
            if (question != null) {
                return question;
            }
        }
        
        return null;
    }
    
    /**
     * Parse LLM response into Question object
     */
    private Question parseQuestionFromResponse(LLMResponse response, 
                                             QuestionType type, 
                                             QuizConfig config) {
        try {
            String responseText = response.getText();
            
            switch (type) {
                case MULTIPLE_CHOICE:
                    return parseMultipleChoiceQuestion(responseText, config);
                case TRUE_FALSE:
                    return parseTrueFalseQuestion(responseText, config);
                case SHORT_ANSWER:
                    return parseShortAnswerQuestion(responseText, config);
                case ESSAY:
                    return parseEssayQuestion(responseText, config);
                case FILL_IN_THE_BLANK:
                    return parseFillInTheBlankQuestion(responseText, config);
                default:
                    return null;
            }
            
        } catch (Exception e) {
            System.err.println("Failed to parse question from response: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Parse multiple choice question from LLM response
     */
    private Question parseMultipleChoiceQuestion(String responseText, QuizConfig config) {
        // Expected format:
        // Question: [question text]
        // A) [option 1]
        // B) [option 2]
        // C) [option 3]
        // D) [option 4]
        // Correct Answer: [A/B/C/D]
        // Explanation: [explanation]
        
        String[] lines = responseText.split("\n");
        String questionText = null;
        List<String> options = new ArrayList<>();
        String correctAnswer = null;
        String explanation = null;
        
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("Question:")) {
                questionText = line.substring(9).trim();
            } else if (line.matches("^[A-D]\\).*")) {
                options.add(line.substring(2).trim());
            } else if (line.startsWith("Correct Answer:")) {
                String answerLetter = line.substring(15).trim();
                if (answerLetter.matches("[A-D]") && !options.isEmpty()) {
                    int index = answerLetter.charAt(0) - 'A';
                    if (index < options.size()) {
                        correctAnswer = options.get(index);
                    }
                }
            } else if (line.startsWith("Explanation:")) {
                explanation = line.substring(12).trim();
            }
        }
        
        if (questionText != null && !options.isEmpty() && correctAnswer != null) {
            return new Question.Builder(questionText, QuestionType.MULTIPLE_CHOICE)
                    .withOptions(options)
                    .withCorrectAnswer(correctAnswer)
                    .withExplanation(explanation)
                    .withSubject(config.getSubject())
                    .withTopic(config.getTopic())
                    .withDifficulty(config.getDifficulty())
                    .withPoints(1.0)
                    .build();
        }
        
        return null;
    }
    
    /**
     * Parse true/false question from LLM response
     */
    private Question parseTrueFalseQuestion(String responseText, QuizConfig config) {
        String[] lines = responseText.split("\n");
        String questionText = null;
        String correctAnswer = null;
        String explanation = null;
        
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("Question:")) {
                questionText = line.substring(9).trim();
            } else if (line.startsWith("Correct Answer:")) {
                String answer = line.substring(15).trim().toLowerCase();
                if (answer.contains("true") || answer.contains("false")) {
                    correctAnswer = answer.contains("true") ? "True" : "False";
                }
            } else if (line.startsWith("Explanation:")) {
                explanation = line.substring(12).trim();
            }
        }
        
        if (questionText != null && correctAnswer != null) {
            return new Question.Builder(questionText, QuestionType.TRUE_FALSE)
                    .withOptions(Arrays.asList("True", "False"))
                    .withCorrectAnswer(correctAnswer)
                    .withExplanation(explanation)
                    .withSubject(config.getSubject())
                    .withTopic(config.getTopic())
                    .withDifficulty(config.getDifficulty())
                    .withPoints(1.0)
                    .build();
        }
        
        return null;
    }
    
    /**
     * Parse short answer question from LLM response
     */
    private Question parseShortAnswerQuestion(String responseText, QuizConfig config) {
        String[] lines = responseText.split("\n");
        String questionText = null;
        String correctAnswer = null;
        String explanation = null;
        List<String> acceptableAnswers = new ArrayList<>();
        
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("Question:")) {
                questionText = line.substring(9).trim();
            } else if (line.startsWith("Answer:")) {
                correctAnswer = line.substring(7).trim();
            } else if (line.startsWith("Acceptable Answers:")) {
                String answers = line.substring(19).trim();
                acceptableAnswers = Arrays.asList(answers.split(",\\s*"));
            } else if (line.startsWith("Explanation:")) {
                explanation = line.substring(12).trim();
            }
        }
        
        if (questionText != null && correctAnswer != null) {
            return new Question.Builder(questionText, QuestionType.SHORT_ANSWER)
                    .withCorrectAnswer(correctAnswer)
                    .withAcceptableAnswers(acceptableAnswers.isEmpty() ? 
                                         Arrays.asList(correctAnswer) : acceptableAnswers)
                    .withExplanation(explanation)
                    .withSubject(config.getSubject())
                    .withTopic(config.getTopic())
                    .withDifficulty(config.getDifficulty())
                    .withPoints(2.0)
                    .build();
        }
        
        return null;
    }
    
    /**
     * Parse essay question from LLM response
     */
    private Question parseEssayQuestion(String responseText, QuizConfig config) {
        String[] lines = responseText.split("\n");
        String questionText = null;
        String explanation = null;
        
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("Question:")) {
                questionText = line.substring(9).trim();
            } else if (line.startsWith("Grading Guidelines:") || line.startsWith("Explanation:")) {
                explanation = line.substring(line.indexOf(":") + 1).trim();
            }
        }
        
        if (questionText != null) {
            return new Question.Builder(questionText, QuestionType.ESSAY)
                    .withExplanation(explanation)
                    .withSubject(config.getSubject())
                    .withTopic(config.getTopic())
                    .withDifficulty(config.getDifficulty())
                    .withPoints(5.0)
                    .withTimeLimit(1800) // 30 minutes for essay
                    .build();
        }
        
        return null;
    }
    
    /**
     * Parse fill-in-the-blank question from LLM response
     */
    private Question parseFillInTheBlankQuestion(String responseText, QuizConfig config) {
        String[] lines = responseText.split("\n");
        String questionText = null;
        String correctAnswer = null;
        String explanation = null;
        
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("Question:")) {
                questionText = line.substring(9).trim();
            } else if (line.startsWith("Answer:")) {
                correctAnswer = line.substring(7).trim();
            } else if (line.startsWith("Explanation:")) {
                explanation = line.substring(12).trim();
            }
        }
        
        if (questionText != null && correctAnswer != null && 
            questionText.contains("_____")) {
            return new Question.Builder(questionText, QuestionType.FILL_IN_THE_BLANK)
                    .withCorrectAnswer(correctAnswer)
                    .withExplanation(explanation)
                    .withSubject(config.getSubject())
                    .withTopic(config.getTopic())
                    .withDifficulty(config.getDifficulty())
                    .withPoints(1.5)
                    .build();
        }
        
        return null;
    }
    
    // Helper methods
    private void initializeDefaultSettings() {
        defaultSettings.put("temperature", 0.7);
        defaultSettings.put("max_tokens", 500);
        defaultSettings.put("top_p", 0.9);
    }
    
    private List<QuestionType> determineQuestionTypes(QuizConfig config) {
        if (config.getQuestionTypes() != null && !config.getQuestionTypes().isEmpty()) {
            return config.getQuestionTypes();
        }
        return getSupportedTypes();
    }
    
    private QuizConfig createContentEnhancedConfig(QuizConfig original, String content) {
        // Create a copy with content-specific enhancements
        QuizConfig enhanced = new QuizConfig.Builder()
                .withSubject(original.getSubject())
                .withTopic(original.getTopic())
                .withLevel(original.getLevel())
                .withQuestionTypes(original.getQuestionTypes())
                .withDifficulty(original.getDifficulty())
                .withQuestionCount(original.getQuestionCount())
                .withCustomSetting("source_content", content)
                .build();
        
        return enhanced;
    }
    
    private Map<String, Object> buildLLMSettings(QuizConfig config) {
        Map<String, Object> settings = new HashMap<>(defaultSettings);
        
        // Adjust temperature based on difficulty
        if (config.getDifficulty() != null) {
            double temperature = 0.5 + (config.getDifficulty().getLevel() * 0.1);
            settings.put("temperature", Math.min(temperature, 1.0));
        }
        
        return settings;
    }
}