package quiz.engine;

import quiz.core.Question;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents an active quiz session for a user
 */
public class QuizSession {
    private String sessionId;
    private String quizId;
    private String userId;
    private Quiz quiz;
    private Map<String, String> userAnswers;
    private Map<String, AnswerResult> answerResults;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private SessionStatus status;
    private int currentQuestionIndex;
    private long timeSpentMs;
    private Map<String, Object> sessionMetadata;
    
    // Constructors
    public QuizSession() {
        this.userAnswers = new HashMap<>();
        this.answerResults = new HashMap<>();
        this.sessionMetadata = new HashMap<>();
        this.status = SessionStatus.NOT_STARTED;
        this.currentQuestionIndex = 0;
    }
    
    public QuizSession(Quiz quiz, String userId) {
        this();
        this.quiz = quiz;
        this.quizId = quiz.getId();
        this.userId = userId;
        this.sessionId = generateSessionId();
    }
    
    // Session management methods
    public void start() {
        if (status != SessionStatus.NOT_STARTED) {
            throw new IllegalStateException("Session already started");
        }
        this.startTime = LocalDateTime.now();
        this.status = SessionStatus.IN_PROGRESS;
    }
    
    public void pause() {
        if (status != SessionStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot pause session that is not in progress");
        }
        this.status = SessionStatus.PAUSED;
        updateTimeSpent();
    }
    
    public void resume() {
        if (status != SessionStatus.PAUSED) {
            throw new IllegalStateException("Cannot resume session that is not paused");
        }
        this.status = SessionStatus.IN_PROGRESS;
    }
    
    public void complete() {
        if (status == SessionStatus.COMPLETED) {
            return; // Already completed
        }
        this.endTime = LocalDateTime.now();
        this.status = SessionStatus.COMPLETED;
        updateTimeSpent();
    }
    
    public void abandon() {
        this.status = SessionStatus.ABANDONED;
        this.endTime = LocalDateTime.now();
        updateTimeSpent();
    }
    
    // Answer management
    public void submitAnswer(String questionId, String answer) {
        if (status != SessionStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot submit answer when session is not in progress");
        }
        userAnswers.put(questionId, answer);
    }
    
    public void setAnswerResult(String questionId, AnswerResult result) {
        answerResults.put(questionId, result);
    }
    
    public String getUserAnswer(String questionId) {
        return userAnswers.get(questionId);
    }
    
    public AnswerResult getAnswerResult(String questionId) {
        return answerResults.get(questionId);
    }
    
    // Navigation methods
    public Question getCurrentQuestion() {
        if (quiz == null || quiz.getQuestions().isEmpty()) {
            return null;
        }
        if (currentQuestionIndex >= quiz.getQuestions().size()) {
            return null;
        }
        return quiz.getQuestions().get(currentQuestionIndex);
    }
    
    public Question getNextQuestion() {
        if (hasNextQuestion()) {
            currentQuestionIndex++;
            return getCurrentQuestion();
        }
        return null;
    }
    
    public Question getPreviousQuestion() {
        if (hasPreviousQuestion()) {
            currentQuestionIndex--;
            return getCurrentQuestion();
        }
        return null;
    }
    
    public boolean hasNextQuestion() {
        return quiz != null && 
               currentQuestionIndex < quiz.getQuestions().size() - 1;
    }
    
    public boolean hasPreviousQuestion() {
        return currentQuestionIndex > 0;
    }
    
    public void goToQuestion(int index) {
        if (index >= 0 && index < quiz.getQuestions().size()) {
            currentQuestionIndex = index;
        }
    }
    
    // Progress tracking
    public double getProgressPercentage() {
        if (quiz == null || quiz.getQuestions().isEmpty()) {
            return 0.0;
        }
        return (double) userAnswers.size() / quiz.getQuestions().size() * 100.0;
    }
    
    public int getAnsweredQuestionCount() {
        return userAnswers.size();
    }
    
    public int getRemainingQuestionCount() {
        return quiz != null ? quiz.getQuestions().size() - userAnswers.size() : 0;
    }
    
    public boolean isAllQuestionsAnswered() {
        return quiz != null && userAnswers.size() == quiz.getQuestions().size();
    }
    
    // Time management
    public long getRemainingTimeMs() {
        if (quiz == null || quiz.getTimeLimit() <= 0 || startTime == null) {
            return -1; // Unlimited time
        }
        
        long elapsedMs = System.currentTimeMillis() - 
                        startTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        long totalTimeMs = quiz.getTimeLimit() * 60 * 1000L;
        
        return Math.max(0, totalTimeMs - elapsedMs);
    }
    
    public boolean isTimeExpired() {
        return getRemainingTimeMs() == 0;
    }
    
    private void updateTimeSpent() {
        if (startTime != null) {
            LocalDateTime now = endTime != null ? endTime : LocalDateTime.now();
            this.timeSpentMs = java.time.Duration.between(startTime, now).toMillis();
        }
    }
    
    // Session statistics
    public Map<String, Object> getSessionStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("session_id", sessionId);
        stats.put("quiz_id", quizId);
        stats.put("user_id", userId);
        stats.put("status", status);
        stats.put("progress_percentage", getProgressPercentage());
        stats.put("answered_questions", getAnsweredQuestionCount());
        stats.put("remaining_questions", getRemainingQuestionCount());
        stats.put("time_spent_ms", timeSpentMs);
        stats.put("remaining_time_ms", getRemainingTimeMs());
        stats.put("current_question_index", currentQuestionIndex);
        return stats;
    }
    
    // Getters and Setters
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getQuizId() { return quizId; }
    public void setQuizId(String quizId) { this.quizId = quizId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }
    
    public Map<String, String> getUserAnswers() { return userAnswers; }
    public void setUserAnswers(Map<String, String> userAnswers) { this.userAnswers = userAnswers; }
    
    public Map<String, AnswerResult> getAnswerResults() { return answerResults; }
    public void setAnswerResults(Map<String, AnswerResult> answerResults) { 
        this.answerResults = answerResults; 
    }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
    public SessionStatus getStatus() { return status; }
    public void setStatus(SessionStatus status) { this.status = status; }
    
    public int getCurrentQuestionIndex() { return currentQuestionIndex; }
    public void setCurrentQuestionIndex(int currentQuestionIndex) { 
        this.currentQuestionIndex = currentQuestionIndex; 
    }
    
    public long getTimeSpentMs() { return timeSpentMs; }
    public void setTimeSpentMs(long timeSpentMs) { this.timeSpentMs = timeSpentMs; }
    
    public Map<String, Object> getSessionMetadata() { return sessionMetadata; }
    public void setSessionMetadata(Map<String, Object> sessionMetadata) { 
        this.sessionMetadata = sessionMetadata; 
    }
    
    private static String generateSessionId() {
        return "SESSION_" + System.currentTimeMillis() + "_" + 
               (int)(Math.random() * 10000);
    }
    
    @Override
    public String toString() {
        return "QuizSession{" +
                "sessionId='" + sessionId + '\'' +
                ", quizId='" + quizId + '\'' +
                ", userId='" + userId + '\'' +
                ", status=" + status +
                ", progress=" + String.format("%.1f", getProgressPercentage()) + "%" +
                '}';
    }
}