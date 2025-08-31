package quiz.core;

/**
 * Enumeration of scoring methods for quizzes
 */
public enum ScoringMethod {
    POINTS_BASED("Points Based", "Each question has assigned points"),
    PERCENTAGE("Percentage", "Score as percentage of total"),
    WEIGHTED("Weighted", "Questions weighted by difficulty"),
    PASS_FAIL("Pass/Fail", "Simple pass or fail based on threshold"),
    SCALED("Scaled", "Scaled scoring with curve adjustment"),
    RUBRIC("Rubric Based", "Custom rubric-based scoring");
    
    private final String displayName;
    private final String description;
    
    ScoringMethod(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean supportsPartialCredit() {
        return this != PASS_FAIL;
    }
    
    public boolean requiresWeights() {
        return this == WEIGHTED || this == RUBRIC;
    }
}