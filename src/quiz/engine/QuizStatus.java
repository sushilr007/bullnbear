package quiz.engine;

/**
 * Enumeration of quiz lifecycle states
 */
public enum QuizStatus {
    DRAFT("Draft", "Quiz is being created or edited"),
    PUBLISHED("Published", "Quiz is available for taking"),
    ARCHIVED("Archived", "Quiz is no longer active"),
    SUSPENDED("Suspended", "Quiz is temporarily unavailable");
    
    private final String displayName;
    private final String description;
    
    QuizStatus(String displayName, String description) {
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
        return this == PUBLISHED;
    }
    
    public boolean canBeEdited() {
        return this == DRAFT;
    }
    
    public boolean canBeTaken() {
        return this == PUBLISHED;
    }
}