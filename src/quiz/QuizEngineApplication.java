package quiz;

import quiz.demo.QuizEngineDemo;

/**
 * Main application class for the Quiz Engine
 */
public class QuizEngineApplication {
    
    public static void main(String[] args) {
        System.out.println("Starting Quiz Engine Application...\n");
        
        try {
            // Run demonstrations
            QuizEngineDemo.main(args);
            QuizEngineDemo.demonstrateQuizSession();
            QuizEngineDemo.demonstrateContentProcessing();
            
            System.out.println("Quiz Engine Application completed successfully!");
            
        } catch (Exception e) {
            System.err.println("Error running Quiz Engine Application: " + e.getMessage());
            e.printStackTrace();
        }
    }
}