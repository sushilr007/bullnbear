package quiz.engine;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents the final result of a completed quiz
 */
public class QuizResult {
    private String resultId;
    private String sessionId;
    private String quizId;
    private String userId;
    private double totalScore;
    private double maxScore;
    private double percentage;
    private String grade;
    private boolean passed;
    private LocalDateTime completedAt;
    private long totalTimeMs;
    private int questionsAnswered;
    private int questionsCorrect;
    private Map<String, AnswerResult> questionResults;
    private Map<String, Object> analytics;
    private String feedback;
    private List<String> recommendations;
    
    // Constructors
    public QuizResult() {
        this.questionResults = new HashMap<>();
        this.analytics = new HashMap<>();
        this.recommendations = new ArrayList<>();
        this.completedAt = LocalDateTime.now();
        this.resultId = generateResultId();
    }
    
    public QuizResult(String sessionId, String quizId, String userId) {
        this();
        this.sessionId = sessionId;
        this.quizId = quizId;
        this.userId = userId;
    }
    
    // Builder pattern
    public static class Builder {
        private QuizResult result;
        
        public Builder(String sessionId, String quizId, String userId) {
            result = new QuizResult(sessionId, quizId, userId);
        }
        
        public Builder withScores(double totalScore, double maxScore) {
            result.totalScore = totalScore;
            result.maxScore = maxScore;
            result.percentage = maxScore > 0 ? (totalScore / maxScore) * 100.0 : 0.0;
            return this;
        }
        
        public Builder withGrade(String grade) {
            result.grade = grade;
            return this;
        }
        
        public Builder withPassed(boolean passed) {
            result.passed = passed;
            return this;
        }
        
        public Builder withTotalTime(long totalTimeMs) {
            result.totalTimeMs = totalTimeMs;
            return this;
        }
        
        public Builder withQuestionCounts(int answered, int correct) {
            result.questionsAnswered = answered;
            result.questionsCorrect = correct;
            return this;
        }
        
        public Builder withQuestionResults(Map<String, AnswerResult> questionResults) {
            result.questionResults = new HashMap<>(questionResults);
            return this;
        }
        
        public Builder addQuestionResult(String questionId, AnswerResult answerResult) {
            result.questionResults.put(questionId, answerResult);
            return this;
        }
        
        public Builder withAnalytics(Map<String, Object> analytics) {
            result.analytics = new HashMap<>(analytics);
            return this;
        }
        
        public Builder withFeedback(String feedback) {
            result.feedback = feedback;
            return this;
        }
        
        public Builder withRecommendations(List<String> recommendations) {
            result.recommendations = new ArrayList<>(recommendations);
            return this;
        }
        
        public Builder addRecommendation(String recommendation) {
            result.recommendations.add(recommendation);
            return this;
        }
        
        public QuizResult build() {
            // Auto-calculate derived values
            result.calculateDerivedMetrics();
            result.generateAnalytics();
            return result;
        }
    }
    
    // Calculation methods
    public void calculateDerivedMetrics() {
        if (maxScore > 0) {
            this.percentage = (totalScore / maxScore) * 100.0;
        }
        
        // Calculate question statistics
        this.questionsAnswered = questionResults.size();
        this.questionsCorrect = (int) questionResults.values().stream()
                                                    .mapToInt(r -> r.isCorrect() ? 1 : 0)
                                                    .sum();
    }
    
    public void generateAnalytics() {
        analytics.put("completion_rate", getCompletionRate());
        analytics.put("accuracy_rate", getAccuracyRate());
        analytics.put("average_time_per_question", getAverageTimePerQuestion());
        analytics.put("performance_by_type", getPerformanceByQuestionType());
        analytics.put("difficulty_analysis", getDifficultyAnalysis());
    }
    
    // Analytics methods
    public double getCompletionRate() {
        // This would be calculated based on total questions in quiz
        return questionsAnswered > 0 ? 100.0 : 0.0;
    }
    
    public double getAccuracyRate() {
        if (questionsAnswered == 0) return 0.0;
        return (double) questionsCorrect / questionsAnswered * 100.0;
    }
    
    public double getAverageTimePerQuestion() {
        if (questionsAnswered == 0) return 0.0;
        return (double) totalTimeMs / questionsAnswered / 1000.0; // seconds
    }
    
    public Map<String, Double> getPerformanceByQuestionType() {
        Map<String, Double> performance = new HashMap<>();
        // This would analyze performance by question type
        // Implementation would group results by question type and calculate averages
        return performance;
    }
    
    public Map<String, Object> getDifficultyAnalysis() {
        Map<String, Object> analysis = new HashMap<>();
        // This would analyze performance by difficulty level
        return analysis;
    }
    
    // Utility methods
    public String getLetterGrade() {
        if (percentage >= 90) return "A";
        if (percentage >= 80) return "B";
        if (percentage >= 70) return "C";
        if (percentage >= 60) return "D";
        return "F";
    }
    
    public String getPerformanceLevel() {
        if (percentage >= 90) return "Excellent";
        if (percentage >= 80) return "Good";
        if (percentage >= 70) return "Satisfactory";
        if (percentage >= 60) return "Needs Improvement";
        return "Poor";
    }
    
    public boolean isHighPerformance() {
        return percentage >= 85.0;
    }
    
    public String getDetailedReport() {
        StringBuilder report = new StringBuilder();
        report.append("Quiz Result Summary\n");
        report.append("===================\n");
        report.append("Score: ").append(String.format("%.1f", totalScore))
              .append("/").append(String.format("%.1f", maxScore))
              .append(" (").append(String.format("%.1f", percentage)).append("%)\n");
        report.append("Grade: ").append(getLetterGrade()).append("\n");
        report.append("Status: ").append(passed ? "PASSED" : "FAILED").append("\n");
        report.append("Questions Correct: ").append(questionsCorrect)
              .append("/").append(questionsAnswered).append("\n");
        report.append("Time Taken: ").append(formatTime(totalTimeMs)).append("\n");
        report.append("Performance Level: ").append(getPerformanceLevel()).append("\n");
        
        if (!recommendations.isEmpty()) {
            report.append("\nRecommendations:\n");
            for (int i = 0; i < recommendations.size(); i++) {
                report.append("• ").append(recommendations.get(i)).append("\n");
            }
        }
        
        return report.toString();
    }
    
    private String formatTime(long timeMs) {
        long seconds = timeMs / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
    
    // Getters and Setters
    public String getResultId() { return resultId; }
    public void setResultId(String resultId) { this.resultId = resultId; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getQuizId() { return quizId; }
    public void setQuizId(String quizId) { this.quizId = quizId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public double getTotalScore() { return totalScore; }
    public void setTotalScore(double totalScore) { this.totalScore = totalScore; }
    
    public double getMaxScore() { return maxScore; }
    public void setMaxScore(double maxScore) { this.maxScore = maxScore; }
    
    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }
    
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    
    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public long getTotalTimeMs() { return totalTimeMs; }
    public void setTotalTimeMs(long totalTimeMs) { this.totalTimeMs = totalTimeMs; }
    
    public int getQuestionsAnswered() { return questionsAnswered; }
    public void setQuestionsAnswered(int questionsAnswered) { this.questionsAnswered = questionsAnswered; }
    
    public int getQuestionsCorrect() { return questionsCorrect; }
    public void setQuestionsCorrect(int questionsCorrect) { this.questionsCorrect = questionsCorrect; }
    
    public Map<String, AnswerResult> getQuestionResults() { return questionResults; }
    public void setQuestionResults(Map<String, AnswerResult> questionResults) { 
        this.questionResults = questionResults; 
    }
    
    public Map<String, Object> getAnalytics() { return analytics; }
    public void setAnalytics(Map<String, Object> analytics) { this.analytics = analytics; }
    
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    
    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
    
    private static String generateResultId() {
        return "RESULT_" + System.currentTimeMillis() + "_" + 
               (int)(Math.random() * 10000);
    }
    
    @Override
    public String toString() {
        return "QuizResult{" +
                "resultId='" + resultId + '\'' +
                ", percentage=" + String.format("%.1f", percentage) + "%" +
                ", grade='" + getLetterGrade() + '\'' +
                ", passed=" + passed +
                ", questionsCorrect=" + questionsCorrect + "/" + questionsAnswered +
                '}';
    }
}