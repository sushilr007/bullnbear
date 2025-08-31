package quiz.engine;

import java.util.List;
import java.util.Map;

/**
 * Manages quiz sessions and user interactions
 */
public interface QuizSessionManager {
    
    /**
     * Create a new quiz session for a user
     * @param quiz Quiz to create session for
     * @param userId User identifier
     * @return Created quiz session
     */
    QuizSession createSession(Quiz quiz, String userId);
    
    /**
     * Get an existing quiz session
     * @param sessionId Session identifier
     * @return Quiz session or null if not found
     */
    QuizSession getSession(String sessionId);
    
    /**
     * Update session with answer result
     * @param session Quiz session to update
     * @param questionId Question that was answered
     * @param result Answer result
     */
    void updateSession(QuizSession session, String questionId, AnswerResult result);
    
    /**
     * Complete a quiz session
     * @param session Session to complete
     * @param finalResult Final quiz result
     */
    void completeSession(QuizSession session, QuizResult finalResult);
    
    /**
     * Pause a quiz session
     * @param sessionId Session to pause
     * @return true if successfully paused
     */
    boolean pauseSession(String sessionId);
    
    /**
     * Resume a paused quiz session
     * @param sessionId Session to resume
     * @return true if successfully resumed
     */
    boolean resumeSession(String sessionId);
    
    /**
     * Abandon a quiz session
     * @param sessionId Session to abandon
     * @return true if successfully abandoned
     */
    boolean abandonSession(String sessionId);
    
    /**
     * Get all sessions for a user
     * @param userId User identifier
     * @return List of user's quiz sessions
     */
    List<QuizSession> getUserSessions(String userId);
    
    /**
     * Get all sessions for a quiz
     * @param quizId Quiz identifier
     * @return List of sessions for the quiz
     */
    List<QuizSession> getQuizSessions(String quizId);
    
    /**
     * Get active sessions
     * @return List of currently active sessions
     */
    List<QuizSession> getActiveSessions();
    
    /**
     * Get count of active sessions
     * @return Number of active sessions
     */
    int getActiveSessionCount();
    
    /**
     * Clean up expired sessions
     * @return Number of sessions cleaned up
     */
    int cleanupExpiredSessions();
    
    /**
     * Get session statistics
     * @return Map of session statistics
     */
    Map<String, Object> getSessionStatistics();
    
    /**
     * Check if user has an active session for a quiz
     * @param userId User identifier
     * @param quizId Quiz identifier
     * @return true if user has active session
     */
    boolean hasActiveSession(String userId, String quizId);
    
    /**
     * Get user's active session for a quiz
     * @param userId User identifier
     * @param quizId Quiz identifier
     * @return Active session or null
     */
    QuizSession getActiveSession(String userId, String quizId);
}