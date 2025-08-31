package quiz.core;

import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

/**
 * Core Question entity representing all types of quiz questions
 */
public class Question {
    private String id;
    private String text;
    private QuestionType type;
    private DifficultyLevel difficulty;
    private String subject;
    private String topic;
    private List<String> options;  // For MCQ
    private String correctAnswer;
    private List<String> acceptableAnswers;  // For flexible matching
    private String explanation;
    private Map<String, Object> metadata;
    private String sourceContent;  // Original content for RAG questions
    private LocalDateTime createdAt;
    private int timeLimit;  // in seconds
    private double points;
    
    // Constructors
    public Question() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Question(String text, QuestionType type) {
        this();
        this.text = text;
        this.type = type;
    }
    
    // Builder pattern for flexible question creation
    public static class Builder {
        private Question question;
        
        public Builder(String text, QuestionType type) {
            question = new Question(text, type);
        }
        
        public Builder withDifficulty(DifficultyLevel difficulty) {
            question.difficulty = difficulty;
            return this;
        }
        
        public Builder withSubject(String subject) {
            question.subject = subject;
            return this;
        }
        
        public Builder withTopic(String topic) {
            question.topic = topic;
            return this;
        }
        
        public Builder withOptions(List<String> options) {
            question.options = options;
            return this;
        }
        
        public Builder withCorrectAnswer(String answer) {
            question.correctAnswer = answer;
            return this;
        }
        
        public Builder withAcceptableAnswers(List<String> answers) {
            question.acceptableAnswers = answers;
            return this;
        }
        
        public Builder withExplanation(String explanation) {
            question.explanation = explanation;
            return this;
        }
        
        public Builder withMetadata(Map<String, Object> metadata) {
            question.metadata = metadata;
            return this;
        }
        
        public Builder withSourceContent(String sourceContent) {
            question.sourceContent = sourceContent;
            return this;
        }
        
        public Builder withTimeLimit(int seconds) {
            question.timeLimit = seconds;
            return this;
        }
        
        public Builder withPoints(double points) {
            question.points = points;
            return this;
        }
        
        public Question build() {
            // Generate ID if not set
            if (question.id == null) {
                question.id = generateId();
            }
            return question;
        }
        
        private String generateId() {
            return "Q_" + System.currentTimeMillis() + "_" + 
                   (int)(Math.random() * 1000);
        }
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    
    public QuestionType getType() { return type; }
    public void setType(QuestionType type) { this.type = type; }
    
    public DifficultyLevel getDifficulty() { return difficulty; }
    public void setDifficulty(DifficultyLevel difficulty) { this.difficulty = difficulty; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
    
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    
    public List<String> getAcceptableAnswers() { return acceptableAnswers; }
    public void setAcceptableAnswers(List<String> acceptableAnswers) { 
        this.acceptableAnswers = acceptableAnswers; 
    }
    
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    public String getSourceContent() { return sourceContent; }
    public void setSourceContent(String sourceContent) { this.sourceContent = sourceContent; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public int getTimeLimit() { return timeLimit; }
    public void setTimeLimit(int timeLimit) { this.timeLimit = timeLimit; }
    
    public double getPoints() { return points; }
    public void setPoints(double points) { this.points = points; }
    
    @Override
    public String toString() {
        return "Question{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                ", type=" + type +
                ", difficulty=" + difficulty +
                ", subject='" + subject + '\'' +
                '}';
    }
}