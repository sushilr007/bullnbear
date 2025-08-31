package quiz.core;

/**
 * Enumeration of education levels for quiz targeting
 */
public enum EducationLevel {
    ELEMENTARY("Elementary School", 1, 5),
    MIDDLE_SCHOOL("Middle School", 6, 8),
    HIGH_SCHOOL("High School", 9, 12),
    UNDERGRADUATE("Undergraduate", 13, 16),
    GRADUATE("Graduate", 17, 20),
    POSTGRADUATE("Postgraduate", 21, 25);
    
    private final String displayName;
    private final int minGrade;
    private final int maxGrade;
    
    EducationLevel(String displayName, int minGrade, int maxGrade) {
        this.displayName = displayName;
        this.minGrade = minGrade;
        this.maxGrade = maxGrade;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public int getMinGrade() {
        return minGrade;
    }
    
    public int getMaxGrade() {
        return maxGrade;
    }
    
    public boolean includesGrade(int grade) {
        return grade >= minGrade && grade <= maxGrade;
    }
    
    public static EducationLevel fromGrade(int grade) {
        for (EducationLevel level : values()) {
            if (level.includesGrade(grade)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Invalid grade level: " + grade);
    }
    
    public DifficultyLevel getDefaultDifficulty() {
        switch (this) {
            case ELEMENTARY:
                return DifficultyLevel.EASY;
            case MIDDLE_SCHOOL:
                return DifficultyLevel.MEDIUM;
            case HIGH_SCHOOL:
                return DifficultyLevel.MEDIUM;
            case UNDERGRADUATE:
                return DifficultyLevel.HARD;
            case GRADUATE:
            case POSTGRADUATE:
                return DifficultyLevel.VERY_HARD;
            default:
                return DifficultyLevel.MEDIUM;
        }
    }
}