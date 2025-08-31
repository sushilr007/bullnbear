package quiz.llm;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * Represents a response from an LLM service
 */
public class LLMResponse {
    private String id;
    private String text;
    private String model;
    private Map<String, Object> metadata;
    private LocalDateTime timestamp;
    private int tokenCount;
    private double confidence;
    private long latencyMs;
    private boolean successful;
    private String errorMessage;
    
    // Constructors
    public LLMResponse() {
        this.metadata = new HashMap<>();
        this.timestamp = LocalDateTime.now();
        this.successful = true;
    }
    
    public LLMResponse(String text, String model) {
        this();
        this.text = text;
        this.model = model;
        this.id = generateId();
    }
    
    // Builder pattern
    public static class Builder {
        private LLMResponse response;
        
        public Builder(String text) {
            response = new LLMResponse();
            response.text = text;
            response.id = generateId();
        }
        
        public Builder withModel(String model) {
            response.model = model;
            return this;
        }
        
        public Builder withMetadata(String key, Object value) {
            response.metadata.put(key, value);
            return this;
        }
        
        public Builder withMetadata(Map<String, Object> metadata) {
            response.metadata.putAll(metadata);
            return this;
        }
        
        public Builder withTokenCount(int tokenCount) {
            response.tokenCount = tokenCount;
            return this;
        }
        
        public Builder withConfidence(double confidence) {
            response.confidence = confidence;
            return this;
        }
        
        public Builder withLatency(long latencyMs) {
            response.latencyMs = latencyMs;
            return this;
        }
        
        public Builder withError(String errorMessage) {
            response.successful = false;
            response.errorMessage = errorMessage;
            return this;
        }
        
        public LLMResponse build() {
            return response;
        }
        
        private static String generateId() {
            return "LLM_" + System.currentTimeMillis() + "_" + 
                   (int)(Math.random() * 10000);
        }
    }
    
    // Utility methods
    public boolean isSuccessful() {
        return successful && text != null && !text.trim().isEmpty();
    }
    
    public boolean hasHighConfidence() {
        return confidence >= 0.8;
    }
    
    public String getQualityScore() {
        if (!isSuccessful()) return "Failed";
        if (confidence >= 0.9) return "Excellent";
        if (confidence >= 0.8) return "Good";
        if (confidence >= 0.7) return "Fair";
        return "Poor";
    }
    
    public boolean isWithinLatencyThreshold(long thresholdMs) {
        return latencyMs <= thresholdMs;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public int getTokenCount() { return tokenCount; }
    public void setTokenCount(int tokenCount) { this.tokenCount = tokenCount; }
    
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    
    public long getLatencyMs() { return latencyMs; }
    public void setLatencyMs(long latencyMs) { this.latencyMs = latencyMs; }
    
    public void setSuccessful(boolean successful) { this.successful = successful; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    private static String generateId() {
        return "LLM_" + System.currentTimeMillis() + "_" + 
               (int)(Math.random() * 10000);
    }
    
    @Override
    public String toString() {
        return "LLMResponse{" +
                "id='" + id + '\'' +
                ", model='" + model + '\'' +
                ", successful=" + successful +
                ", confidence=" + confidence +
                ", latencyMs=" + latencyMs +
                ", tokenCount=" + tokenCount +
                '}';
    }
}