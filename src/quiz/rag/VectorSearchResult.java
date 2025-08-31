package quiz.rag;

import java.util.Map;

/**
 * Represents a result from vector similarity search
 */
public class VectorSearchResult {
    private String id;
    private String contentChunkId;
    private double similarityScore;
    private float[] embedding;
    private Map<String, Object> metadata;
    private String content; // Optional: actual content for convenience
    
    // Constructors
    public VectorSearchResult() {}
    
    public VectorSearchResult(String id, String contentChunkId, double similarityScore) {
        this.id = id;
        this.contentChunkId = contentChunkId;
        this.similarityScore = similarityScore;
    }
    
    // Builder pattern
    public static class Builder {
        private VectorSearchResult result;
        
        public Builder(String id, String contentChunkId, double similarityScore) {
            result = new VectorSearchResult(id, contentChunkId, similarityScore);
        }
        
        public Builder withEmbedding(float[] embedding) {
            result.embedding = embedding;
            return this;
        }
        
        public Builder withMetadata(Map<String, Object> metadata) {
            result.metadata = metadata;
            return this;
        }
        
        public Builder withContent(String content) {
            result.content = content;
            return this;
        }
        
        public VectorSearchResult build() {
            return result;
        }
    }
    
    // Utility methods
    public boolean isRelevant(double threshold) {
        return similarityScore >= threshold;
    }
    
    public String getRelevanceLevel() {
        if (similarityScore >= 0.9) return "Very High";
        if (similarityScore >= 0.8) return "High";
        if (similarityScore >= 0.7) return "Medium";
        if (similarityScore >= 0.6) return "Low";
        return "Very Low";
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getContentChunkId() { return contentChunkId; }
    public void setContentChunkId(String contentChunkId) { this.contentChunkId = contentChunkId; }
    
    public double getSimilarityScore() { return similarityScore; }
    public void setSimilarityScore(double similarityScore) { this.similarityScore = similarityScore; }
    
    public float[] getEmbedding() { return embedding; }
    public void setEmbedding(float[] embedding) { this.embedding = embedding; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    @Override
    public String toString() {
        return "VectorSearchResult{" +
                "id='" + id + '\'' +
                ", contentChunkId='" + contentChunkId + '\'' +
                ", similarityScore=" + similarityScore +
                ", relevanceLevel='" + getRelevanceLevel() + '\'' +
                '}';
    }
}