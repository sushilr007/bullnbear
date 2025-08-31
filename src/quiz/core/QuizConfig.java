package quiz.core;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Configuration class for quiz generation
 * Supports dynamic configuration for different quiz types
 */
public class QuizConfig {
    private String id;
    private String subject;
    private String topic;
    private EducationLevel level;
    private List<QuestionType> questionTypes;
    private GeneratorType preferredGenerator;
    private DifficultyLevel difficulty;
    private int questionCount;
    private int timeLimit; // in minutes
    private boolean shuffleQuestions;
    private boolean shuffleOptions;
    private Map<String, Object> customSettings;
    private List<String> contentSources; // For RAG
    private String templateId; // For LLM
    private ScoringMethod scoringMethod;
    private boolean allowPartialCredit;
    private double passingScore;
    
    // Constructors
    public QuizConfig() {
        this.customSettings = new HashMap<>();
        this.shuffleQuestions = true;
        this.shuffleOptions = true;
        this.allowPartialCredit = true;
        this.passingScore = 60.0;
    }
    
    public QuizConfig(String subject, EducationLevel level, int questionCount) {
        this();
        this.subject = subject;
        this.level = level;
        this.questionCount = questionCount;
    }
    
    // Builder pattern for flexible configuration
    public static class Builder {
        private QuizConfig config;
        
        public Builder() {
            config = new QuizConfig();
        }
        
        public Builder withSubject(String subject) {
            config.subject = subject;
            return this;
        }
        
        public Builder withTopic(String topic) {
            config.topic = topic;
            return this;
        }
        
        public Builder withLevel(EducationLevel level) {
            config.level = level;
            return this;
        }
        
        public Builder withQuestionTypes(List<QuestionType> types) {
            config.questionTypes = types;
            return this;
        }
        
        public Builder withPreferredGenerator(GeneratorType generator) {
            config.preferredGenerator = generator;
            return this;
        }
        
        public Builder withDifficulty(DifficultyLevel difficulty) {
            config.difficulty = difficulty;
            return this;
        }
        
        public Builder withQuestionCount(int count) {
            config.questionCount = count;
            return this;
        }
        
        public Builder withTimeLimit(int minutes) {
            config.timeLimit = minutes;
            return this;
        }
        
        public Builder withShuffleQuestions(boolean shuffle) {
            config.shuffleQuestions = shuffle;
            return this;
        }
        
        public Builder withShuffleOptions(boolean shuffle) {
            config.shuffleOptions = shuffle;
            return this;
        }
        
        public Builder withContentSources(List<String> sources) {
            config.contentSources = sources;
            return this;
        }
        
        public Builder withTemplateId(String templateId) {
            config.templateId = templateId;
            return this;
        }
        
        public Builder withScoringMethod(ScoringMethod method) {
            config.scoringMethod = method;
            return this;
        }
        
        public Builder withPartialCredit(boolean allow) {
            config.allowPartialCredit = allow;
            return this;
        }
        
        public Builder withPassingScore(double score) {
            config.passingScore = score;
            return this;
        }
        
        public Builder withCustomSetting(String key, Object value) {
            config.customSettings.put(key, value);
            return this;
        }
        
        public QuizConfig build() {
            // Generate ID if not set
            if (config.id == null) {
                config.id = generateId();
            }
            return config;
        }
        
        private String generateId() {
            return "CONFIG_" + System.currentTimeMillis();
        }
    }
    
    // Validation methods
    public boolean isValid() {
        return subject != null && !subject.trim().isEmpty() &&
               level != null &&
               questionCount > 0;
    }
    
    public String getValidationMessage() {
        if (subject == null || subject.trim().isEmpty()) {
            return "Subject is required";
        }
        if (level == null) {
            return "Education level is required";
        }
        if (questionCount <= 0) {
            return "Question count must be positive";
        }
        return null;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    
    public EducationLevel getLevel() { return level; }
    public void setLevel(EducationLevel level) { this.level = level; }
    
    public List<QuestionType> getQuestionTypes() { return questionTypes; }
    public void setQuestionTypes(List<QuestionType> questionTypes) { 
        this.questionTypes = questionTypes; 
    }
    
    public GeneratorType getPreferredGenerator() { return preferredGenerator; }
    public void setPreferredGenerator(GeneratorType preferredGenerator) { 
        this.preferredGenerator = preferredGenerator; 
    }
    
    public DifficultyLevel getDifficulty() { return difficulty; }
    public void setDifficulty(DifficultyLevel difficulty) { this.difficulty = difficulty; }
    
    public int getQuestionCount() { return questionCount; }
    public void setQuestionCount(int questionCount) { this.questionCount = questionCount; }
    
    public int getTimeLimit() { return timeLimit; }
    public void setTimeLimit(int timeLimit) { this.timeLimit = timeLimit; }
    
    public boolean isShuffleQuestions() { return shuffleQuestions; }
    public void setShuffleQuestions(boolean shuffleQuestions) { 
        this.shuffleQuestions = shuffleQuestions; 
    }
    
    public boolean isShuffleOptions() { return shuffleOptions; }
    public void setShuffleOptions(boolean shuffleOptions) { this.shuffleOptions = shuffleOptions; }
    
    public Map<String, Object> getCustomSettings() { return customSettings; }
    public void setCustomSettings(Map<String, Object> customSettings) { 
        this.customSettings = customSettings; 
    }
    
    public List<String> getContentSources() { return contentSources; }
    public void setContentSources(List<String> contentSources) { 
        this.contentSources = contentSources; 
    }
    
    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }
    
    public ScoringMethod getScoringMethod() { return scoringMethod; }
    public void setScoringMethod(ScoringMethod scoringMethod) { this.scoringMethod = scoringMethod; }
    
    public boolean isAllowPartialCredit() { return allowPartialCredit; }
    public void setAllowPartialCredit(boolean allowPartialCredit) { 
        this.allowPartialCredit = allowPartialCredit; 
    }
    
    public double getPassingScore() { return passingScore; }
    public void setPassingScore(double passingScore) { this.passingScore = passingScore; }
    
    @Override
    public String toString() {
        return "QuizConfig{" +
                "id='" + id + '\'' +
                ", subject='" + subject + '\'' +
                ", level=" + level +
                ", questionCount=" + questionCount +
                ", difficulty=" + difficulty +
                '}';
    }
}