package quiz.engine;

import quiz.core.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a complete quiz with questions and metadata
 */
public class Quiz {
    private String id;
    private String title;
    private String description;
    private QuizConfig config;
    private List<Question> questions;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private QuizStatus status;
    private int timeLimit; // in minutes
    private double totalPoints;
    private String instructions;
    
    // Constructors
    public Quiz() {
        this.questions = new ArrayList<>();
        this.metadata = new HashMap<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = QuizStatus.DRAFT;
    }
    
    public Quiz(QuizConfig config) {
        this();
        this.config = config;
        this.id = generateId();
        this.title = generateTitle(config);
    }
    
    // Builder pattern
    public static class Builder {
        private Quiz quiz;
        
        public Builder(QuizConfig config) {
            quiz = new Quiz(config);
        }
        
        public Builder withTitle(String title) {
            quiz.title = title;
            return this;
        }
        
        public Builder withDescription(String description) {
            quiz.description = description;
            return this;
        }
        
        public Builder withQuestions(List<Question> questions) {
            quiz.questions = new ArrayList<>(questions);
            quiz.calculateTotalPoints();
            return this;
        }
        
        public Builder addQuestion(Question question) {
            quiz.questions.add(question);
            quiz.calculateTotalPoints();
            return this;
        }
        
        public Builder withMetadata(String key, Object value) {
            quiz.metadata.put(key, value);
            return this;
        }
        
        public Builder withCreatedBy(String createdBy) {
            quiz.createdBy = createdBy;
            return this;
        }
        
        public Builder withTimeLimit(int minutes) {
            quiz.timeLimit = minutes;
            return this;
        }
        
        public Builder withInstructions(String instructions) {
            quiz.instructions = instructions;
            return this;
        }
        
        public Builder withStatus(QuizStatus status) {
            quiz.status = status;
            return this;
        }
        
        public Quiz build() {
            quiz.updatedAt = LocalDateTime.now();
            if (quiz.instructions == null) {
                quiz.instructions = generateDefaultInstructions();
            }
            return quiz;
        }
        
        private String generateDefaultInstructions() {
            StringBuilder instructions = new StringBuilder();
            instructions.append("Welcome to the ").append(quiz.title).append(" quiz.\n\n");
            instructions.append("Instructions:\n");
            instructions.append("• Read each question carefully\n");
            instructions.append("• Select the best answer for multiple choice questions\n");
            instructions.append("• Provide clear and concise answers for short answer questions\n");
            
            if (quiz.timeLimit > 0) {
                instructions.append("• You have ").append(quiz.timeLimit)
                           .append(" minutes to complete this quiz\n");
            }
            
            instructions.append("• Review your answers before submitting\n");
            instructions.append("\nGood luck!");
            
            return instructions.toString();
        }
    }
    
    // Utility methods
    public void calculateTotalPoints() {
        this.totalPoints = questions.stream()
                                   .mapToDouble(Question::getPoints)
                                   .sum();
    }
    
    public int getQuestionCount() {
        return questions.size();
    }
    
    public List<QuestionType> getQuestionTypes() {
        return questions.stream()
                       .map(Question::getType)
                       .distinct()
                       .collect(Collectors.toList());
    }
    
    public Question getQuestionById(String questionId) {
        return questions.stream()
                       .filter(q -> q.getId().equals(questionId))
                       .findFirst()
                       .orElse(null);
    }
    
    public List<Question> getQuestionsByType(QuestionType type) {
        return questions.stream()
                       .filter(q -> q.getType() == type)
                       .collect(Collectors.toList());
    }
    
    public boolean isTimeLimited() {
        return timeLimit > 0;
    }
    
    public boolean isPublished() {
        return status == QuizStatus.PUBLISHED;
    }
    
    public boolean isDraft() {
        return status == QuizStatus.DRAFT;
    }
    
    public void publish() {
        if (questions.isEmpty()) {
            throw new IllegalStateException("Cannot publish quiz without questions");
        }
        this.status = QuizStatus.PUBLISHED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void archive() {
        this.status = QuizStatus.ARCHIVED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("question_count", getQuestionCount());
        stats.put("total_points", totalPoints);
        stats.put("question_types", getQuestionTypes());
        stats.put("average_points_per_question", totalPoints / getQuestionCount());
        stats.put("estimated_duration", estimateDuration());
        return stats;
    }
    
    private int estimateDuration() {
        // Estimate based on question types and count
        int estimatedMinutes = 0;
        for (Question question : questions) {
            switch (question.getType()) {
                case ESSAY:
                    estimatedMinutes += 15;
                    break;
                case SHORT_ANSWER:
                    estimatedMinutes += 3;
                    break;
                case FILL_IN_THE_BLANK:
                    estimatedMinutes += 2;
                    break;
                case MULTIPLE_CHOICE:
                case TRUE_FALSE:
                    estimatedMinutes += 1;
                    break;
            }
        }
        return Math.max(estimatedMinutes, 5); // Minimum 5 minutes
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public QuizConfig getConfig() { return config; }
    public void setConfig(QuizConfig config) { this.config = config; }
    
    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { 
        this.questions = questions;
        calculateTotalPoints();
    }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public QuizStatus getStatus() { return status; }
    public void setStatus(QuizStatus status) { this.status = status; }
    
    public int getTimeLimit() { return timeLimit; }
    public void setTimeLimit(int timeLimit) { this.timeLimit = timeLimit; }
    
    public double getTotalPoints() { return totalPoints; }
    public void setTotalPoints(double totalPoints) { this.totalPoints = totalPoints; }
    
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    
    // Helper methods
    private static String generateId() {
        return "QUIZ_" + System.currentTimeMillis() + "_" + 
               (int)(Math.random() * 10000);
    }
    
    private String generateTitle(QuizConfig config) {
        StringBuilder title = new StringBuilder();
        title.append(config.getSubject() != null ? config.getSubject() : "General");
        
        if (config.getTopic() != null) {
            title.append(" - ").append(config.getTopic());
        }
        
        title.append(" Quiz");
        
        if (config.getLevel() != null) {
            title.append(" (").append(config.getLevel().getDisplayName()).append(")");
        }
        
        return title.toString();
    }
    
    @Override
    public String toString() {
        return "Quiz{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", questionCount=" + getQuestionCount() +
                ", totalPoints=" + totalPoints +
                ", status=" + status +
                '}';
    }
}