package quiz.engine;

/**
 * Interface for evaluating quiz answers and calculating scores
 */
public interface QuizEvaluator {
    
    /**
     * Evaluate a single answer
     * @param session Quiz session context
     * @param questionId Question being answered
     * @param userAnswer User's answer
     * @return Answer evaluation result
     */
    AnswerResult evaluateAnswer(QuizSession session, String questionId, String userAnswer);
    
    /**
     * Calculate final quiz score
     * @param session Completed quiz session
     * @return Final quiz result
     */
    QuizResult calculateFinalScore(QuizSession session);
    
    /**
     * Evaluate multiple choice answer
     * @param userAnswer User's selected option
     * @param correctAnswer Correct option
     * @param maxScore Maximum points for the question
     * @return Answer result
     */
    AnswerResult evaluateMultipleChoice(String userAnswer, String correctAnswer, double maxScore);
    
    /**
     * Evaluate true/false answer
     * @param userAnswer User's answer (True/False)
     * @param correctAnswer Correct answer
     * @param maxScore Maximum points for the question
     * @return Answer result
     */
    AnswerResult evaluateTrueFalse(String userAnswer, String correctAnswer, double maxScore);
    
    /**
     * Evaluate short answer with flexible matching
     * @param userAnswer User's answer
     * @param correctAnswer Expected answer
     * @param acceptableAnswers List of acceptable alternatives
     * @param maxScore Maximum points for the question
     * @return Answer result
     */
    AnswerResult evaluateShortAnswer(String userAnswer, String correctAnswer, 
                                   java.util.List<String> acceptableAnswers, double maxScore);
    
    /**
     * Evaluate fill-in-the-blank answer
     * @param userAnswer User's answer
     * @param correctAnswer Correct answer
     * @param maxScore Maximum points for the question
     * @return Answer result
     */
    AnswerResult evaluateFillInTheBlank(String userAnswer, String correctAnswer, double maxScore);
    
    /**
     * Evaluate essay answer (may require manual review)
     * @param userAnswer User's essay
     * @param gradingGuidelines Grading guidelines
     * @param maxScore Maximum points for the question
     * @return Answer result (may be partial/pending)
     */
    AnswerResult evaluateEssay(String userAnswer, String gradingGuidelines, double maxScore);
    
    /**
     * Calculate partial credit for an answer
     * @param userAnswer User's answer
     * @param correctAnswer Correct answer
     * @param maxScore Maximum possible score
     * @return Partial credit score
     */
    double calculatePartialCredit(String userAnswer, String correctAnswer, double maxScore);
    
    /**
     * Generate feedback for an answer
     * @param result Answer result
     * @param question Original question
     * @return Generated feedback message
     */
    String generateFeedback(AnswerResult result, quiz.core.Question question);
}