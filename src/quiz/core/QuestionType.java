package quiz.core;

/**
 * Enumeration of supported question types
 */
public enum QuestionType {
    MULTIPLE_CHOICE("Multiple Choice", "mcq"),
    TRUE_FALSE("True/False", "tf"),
    SHORT_ANSWER("Short Answer", "sa"),
    ESSAY("Essay", "essay"),
    FILL_IN_THE_BLANK("Fill in the Blank", "fitb"),
    MATCHING("Matching", "match"),
    ORDERING("Ordering", "order"),
    NUMERICAL("Numerical", "num");
    
    private final String displayName;
    private final String code;
    
    QuestionType(String displayName, String code) {
        this.displayName = displayName;
        this.code = code;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getCode() {
        return code;
    }
    
    public static QuestionType fromCode(String code) {
        for (QuestionType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown question type code: " + code);
    }
    
    public boolean requiresOptions() {
        return this == MULTIPLE_CHOICE || this == MATCHING;
    }
    
    public boolean isObjective() {
        return this != ESSAY && this != SHORT_ANSWER;
    }
    
    public boolean supportsAutoGrading() {
        return this != ESSAY;
    }
}