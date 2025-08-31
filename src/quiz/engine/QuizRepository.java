package quiz.engine;

import quiz.core.QuizConfig;
import java.util.List;
import java.util.Map;

/**
 * Repository interface for quiz persistence and retrieval
 */
public interface QuizRepository {
    
    /**
     * Save a quiz
     * @param quiz Quiz to save
     * @return Saved quiz with updated metadata
     */
    Quiz save(Quiz quiz);
    
    /**
     * Find quiz by ID
     * @param quizId Quiz identifier
     * @return Quiz or null if not found
     */
    Quiz findById(String quizId);
    
    /**
     * Find quizzes by subject
     * @param subject Subject filter
     * @param limit Maximum number of results
     * @return List of matching quizzes
     */
    List<Quiz> findBySubject(String subject, int limit);
    
    /**
     * Find quizzes by education level
     * @param level Education level filter
     * @param limit Maximum number of results
     * @return List of matching quizzes
     */
    List<Quiz> findByEducationLevel(quiz.core.EducationLevel level, int limit);
    
    /**
     * Find quizzes by creator
     * @param createdBy Creator identifier
     * @param limit Maximum number of results
     * @return List of quizzes created by user
     */
    List<Quiz> findByCreator(String createdBy, int limit);
    
    /**
     * Find quizzes by status
     * @param status Quiz status filter
     * @param limit Maximum number of results
     * @return List of quizzes with specified status
     */
    List<Quiz> findByStatus(QuizStatus status, int limit);
    
    /**
     * Search quizzes by criteria
     * @param criteria Search criteria
     * @param limit Maximum number of results
     * @return List of matching quizzes
     */
    List<Quiz> search(Map<String, Object> criteria, int limit);
    
    /**
     * Update an existing quiz
     * @param quiz Quiz to update
     * @return Updated quiz
     */
    Quiz update(Quiz quiz);
    
    /**
     * Delete a quiz
     * @param quizId Quiz identifier
     * @return true if deletion was successful
     */
    boolean delete(String quizId);
    
    /**
     * Get all quizzes (paginated)
     * @param offset Starting offset
     * @param limit Maximum number of results
     * @return List of quizzes
     */
    List<Quiz> findAll(int offset, int limit);
    
    /**
     * Get total count of quizzes
     * @return Total number of quizzes
     */
    long getTotalCount();
    
    /**
     * Get count by status
     * @param status Quiz status
     * @return Count of quizzes with specified status
     */
    long getCountByStatus(QuizStatus status);
    
    /**
     * Get repository statistics
     * @return Map of repository statistics
     */
    Map<String, Object> getStatistics();
    
    /**
     * Check if quiz exists
     * @param quizId Quiz identifier
     * @return true if quiz exists
     */
    boolean exists(String quizId);
    
    /**
     * Archive old quizzes
     * @param olderThanDays Archive quizzes older than specified days
     * @return Number of quizzes archived
     */
    int archiveOldQuizzes(int olderThanDays);
}