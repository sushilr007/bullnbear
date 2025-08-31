package quiz.rag;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * Represents a chunk of processed content with metadata
 */
public class ContentChunk {
    private String id;
    private String content;
    private String subject;
    private String topic;
    private String sourceDocument;
    private int chunkIndex;
    private int startPosition;
    private int endPosition;
    private Map<String, Object> metadata;
    private float[] embedding;
    private LocalDateTime createdAt;
    private double relevanceScore;
    
    // Constructors
    public ContentChunk() {
        this.metadata = new HashMap<>();
        this.createdAt = LocalDateTime.now();
    }
    
    public ContentChunk(String content, String subject) {
        this();
        this.content = content;
        this.subject = subject;
        this.id = generateId();
    }
    
    // Builder pattern
    public static class Builder {
        private ContentChunk chunk;
        
        public Builder(String content) {
            chunk = new ContentChunk();
            chunk.content = content;
            chunk.id = generateId();
        }
        
        public Builder withSubject(String subject) {
            chunk.subject = subject;
            return this;
        }
        
        public Builder withTopic(String topic) {
            chunk.topic = topic;
            return this;
        }
        
        public Builder withSourceDocument(String sourceDocument) {
            chunk.sourceDocument = sourceDocument;
            return this;
        }
        
        public Builder withChunkIndex(int index) {
            chunk.chunkIndex = index;
            return this;
        }
        
        public Builder withPosition(int start, int end) {
            chunk.startPosition = start;
            chunk.endPosition = end;
            return this;
        }
        
        public Builder withMetadata(String key, Object value) {
            chunk.metadata.put(key, value);
            return this;
        }
        
        public Builder withMetadata(Map<String, Object> metadata) {
            chunk.metadata.putAll(metadata);
            return this;
        }
        
        public Builder withEmbedding(float[] embedding) {
            chunk.embedding = embedding;
            return this;
        }
        
        public Builder withRelevanceScore(double score) {
            chunk.relevanceScore = score;
            return this;
        }
        
        public ContentChunk build() {
            return chunk;
        }
        
        private static String generateId() {
            return "CHUNK_" + System.currentTimeMillis() + "_" + 
                   (int)(Math.random() * 10000);
        }
    }
    
    // Utility methods
    public int getWordCount() {
        if (content == null) return 0;
        return content.trim().split("\\s+").length;
    }
    
    public int getCharacterCount() {
        return content != null ? content.length() : 0;
    }
    
    public boolean hasEmbedding() {
        return embedding != null && embedding.length > 0;
    }
    
    public String getPreview(int maxLength) {
        if (content == null) return "";
        if (content.length() <= maxLength) return content;
        return content.substring(0, maxLength) + "...";
    }
    
    public boolean containsKeywords(String... keywords) {
        if (content == null) return false;
        String lowerContent = content.toLowerCase();
        for (String keyword : keywords) {
            if (lowerContent.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    
    public String getSourceDocument() { return sourceDocument; }
    public void setSourceDocument(String sourceDocument) { this.sourceDocument = sourceDocument; }
    
    public int getChunkIndex() { return chunkIndex; }
    public void setChunkIndex(int chunkIndex) { this.chunkIndex = chunkIndex; }
    
    public int getStartPosition() { return startPosition; }
    public void setStartPosition(int startPosition) { this.startPosition = startPosition; }
    
    public int getEndPosition() { return endPosition; }
    public void setEndPosition(int endPosition) { this.endPosition = endPosition; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    public float[] getEmbedding() { return embedding; }
    public void setEmbedding(float[] embedding) { this.embedding = embedding; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public double getRelevanceScore() { return relevanceScore; }
    public void setRelevanceScore(double relevanceScore) { this.relevanceScore = relevanceScore; }
    
    private static String generateId() {
        return "CHUNK_" + System.currentTimeMillis() + "_" + 
               (int)(Math.random() * 10000);
    }
    
    @Override
    public String toString() {
        return "ContentChunk{" +
                "id='" + id + '\'' +
                ", subject='" + subject + '\'' +
                ", topic='" + topic + '\'' +
                ", wordCount=" + getWordCount() +
                ", relevanceScore=" + relevanceScore +
                '}';
    }
}