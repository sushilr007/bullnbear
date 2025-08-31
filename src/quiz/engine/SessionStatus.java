package quiz.engine;

/**
 * Enumeration of quiz session states
 */
public enum SessionStatus {
    NOT_STARTED("Not Started", "Session has been created but not yet started"),
    IN_PROGRESS("In Progress", "User is actively taking the quiz"),
    PAUSED("Paused", "Session is temporarily paused"),
    COMPLETED("Completed", "Quiz has been finished and submitted"),
    ABANDONED("Abandoned", "Session was started but not completed"),
    EXPIRED("Expired", "Session time limit has been exceeded");
    
    private final String displayName;
    private final String description;
    
    SessionStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isActive() {
        return this == IN_PROGRESS || this == PAUSED;
    }
    
    public boolean isFinished() {
        return this == COMPLETED || this == ABANDONED || this == EXPIRED;
    }
    
    public boolean canBeResumed() {
        return this == PAUSED;
    }
    
    public boolean canSubmitAnswers() {
        return this == IN_PROGRESS;
    }
}