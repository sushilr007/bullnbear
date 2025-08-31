package quiz.core;

/**
 * Enumeration of question difficulty levels
 */
public enum DifficultyLevel {
    VERY_EASY(1, "Very Easy"),
    EASY(2, "Easy"),
    MEDIUM(3, "Medium"),
    HARD(4, "Hard"),
    VERY_HARD(5, "Very Hard");
    
    private final int level;
    private final String displayName;
    
    DifficultyLevel(int level, String displayName) {
        this.level = level;
        this.displayName = displayName;
    }
    
    public int getLevel() {
        return level;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static DifficultyLevel fromLevel(int level) {
        for (DifficultyLevel difficulty : values()) {
            if (difficulty.level == level) {
                return difficulty;
            }
        }
        throw new IllegalArgumentException("Invalid difficulty level: " + level);
    }
    
    public boolean isEasierThan(DifficultyLevel other) {
        return this.level < other.level;
    }
    
    public boolean isHarderThan(DifficultyLevel other) {
        return this.level > other.level;
    }
}