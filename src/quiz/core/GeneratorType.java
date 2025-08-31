package quiz.core;

/**
 * Enumeration of question generation strategies
 */
public enum GeneratorType {
    RAG("Retrieval-Augmented Generation", "Uses document retrieval and generation"),
    LLM("Large Language Model", "Uses pure LLM generation"),
    HYBRID("Hybrid Approach", "Combines RAG and LLM strategies"),
    TEMPLATE("Template-based", "Uses predefined question templates"),
    MANUAL("Manual Entry", "Manually created questions");
    
    private final String displayName;
    private final String description;
    
    GeneratorType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean requiresContent() {
        return this == RAG || this == HYBRID;
    }
    
    public boolean requiresLLM() {
        return this == LLM || this == HYBRID;
    }
    
    public boolean supportsRealTime() {
        return this != MANUAL;
    }
}