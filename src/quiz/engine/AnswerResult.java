package quiz.engine;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * Represents the result of evaluating a user's answer to a question
 */
public class AnswerResult {
    private String questionId;
    private String userAnswer;
    private String correctAnswer;
    private boolean isCorrect;
    private double score;
    private double maxScore;
    private String feedback;
    private String explanation;
    private LocalDateTime submittedAt;
    private long timeSpentMs;
    private Map<String, Object> metadata;
    
    // Constructors
    public AnswerResult() {
        this.metadata = new HashMap<>();
        this.submittedAt = LocalDateTime.now();
    }
    
    public AnswerResult(String questionId, String userAnswer, boolean isCorrect) {
        this();
        this.questionId = questionId;
        this.userAnswer = userAnswer;
        this.isCorrect = isCorrect;
    }
    
    // Builder pattern
    public static class Builder {
        private AnswerResult result;
        
        public Builder(String questionId, String userAnswer) {
            result = new AnswerResult();
            result.questionId = questionId;
            result.userAnswer = userAnswer;
        }
        
        public Builder withCorrectAnswer(String correctAnswer) {
            result.correctAnswer = correctAnswer;
            return this;
        }
        
        public Builder withCorrectness(boolean isCorrect) {
            result.isCorrect = isCorrect;
            return this;
        }
        
        public Builder withScore(double score, double maxScore) {
            result.score = score;
            result.maxScore = maxScore;
            return this;
        }
        
        public Builder withFeedback(String feedback) {
            result.feedback = feedback;
            return this;
        }
        
        public Builder withExplanation(String explanation) {
            result.explanation = explanation;
            return this;
        }
        
        public Builder withTimeSpent(long timeSpentMs) {
            result.timeSpentMs = timeSpentMs;
            return this;
        }
        
        public Builder withMetadata(String key, Object value) {
            result.metadata.put(key, value);
            return this;
        }
        
        public AnswerResult build() {
            // Auto-calculate correctness if not set
            if (result.correctAnswer != null && result.userAnswer != null) {
                if (!result.metadata.containsKey("correctness_calculated")) {
                    result.isCorrect = calculateCorrectness();
                    result.metadata.put("correctness_calculated", true);
                }
            }
            
            // Auto-calculate score if not set
            if (result.maxScore > 0 && result.score == 0) {
                result.score = result.isCorrect ? result.maxScore : 0;
            }
            
            return result;
        }
        
        private boolean calculateCorrectness() {
            if (result.userAnswer == null || result.correctAnswer == null) {
                return false;
            }
            
            String userAns = result.userAnswer.trim().toLowerCase();
            String correctAns = result.correctAnswer.trim().toLowerCase();
            
            return userAns.equals(correctAns);
        }
    }
    
    // Utility methods
    public double getScorePercentage() {
        if (maxScore == 0) return 0.0;
        return (score / maxScore) * 100.0;
    }
    
    public boolean isPartialCredit() {
        return score > 0 && score < maxScore;
    }
    
    public String getPerformanceLevel() {
        double percentage = getScorePercentage();
        if (percentage >= 90) return "Excellent";
        if (percentage >= 80) return "Good";
        if (percentage >= 70) return "Satisfactory";
        if (percentage >= 60) return "Needs Improvement";
        return "Poor";
    }
    
    public boolean isTimely() {
        // Consider answer timely if submitted within reasonable time
        // This is a simple heuristic - could be more sophisticated
        return timeSpentMs < 300000; // 5 minutes
    }
    
    public Map<String, Object> getAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("question_id", questionId);
        analytics.put("is_correct", isCorrect);
        analytics.put("score_percentage", getScorePercentage());
        analytics.put("performance_level", getPerformanceLevel());
        analytics.put("time_spent_seconds", timeSpentMs / 1000.0);
        analytics.put("is_timely", isTimely());
        analytics.put("has_partial_credit", isPartialCredit());
        return analytics;
    }
    
    // Getters and Setters
    public String getQuestionId() { return questionId; }
    public void setQuestionId(String questionId) { this.questionId = questionId; }
    
    public String getUserAnswer() { return userAnswer; }
    public void setUserAnswer(String userAnswer) { this.userAnswer = userAnswer; }
    
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    
    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { isCorrect = correct; }
    
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
    
    public double getMaxScore() { return maxScore; }
    public void setMaxScore(double maxScore) { this.maxScore = maxScore; }
    
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    
    public long getTimeSpentMs() { return timeSpentMs; }
    public void setTimeSpentMs(long timeSpentMs) { this.timeSpentMs = timeSpentMs; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    @Override
    public String toString() {
        return "AnswerResult{" +
                "questionId='" + questionId + '\'' +
                ", isCorrect=" + isCorrect +
                ", score=" + score + "/" + maxScore +
                ", performance='" + getPerformanceLevel() + '\'' +
                '}';
    }
}