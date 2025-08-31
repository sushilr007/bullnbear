package quiz.llm;

import java.time.LocalDateTime;

/**
 * Represents a message in a chat-style LLM interaction
 */
public class ChatMessage {
    private String role; // "system", "user", "assistant"
    private String content;
    private LocalDateTime timestamp;
    
    // Constructors
    public ChatMessage() {
        this.timestamp = LocalDateTime.now();
    }
    
    public ChatMessage(String role, String content) {
        this();
        this.role = role;
        this.content = content;
    }
    
    // Factory methods for common message types
    public static ChatMessage system(String content) {
        return new ChatMessage("system", content);
    }
    
    public static ChatMessage user(String content) {
        return new ChatMessage("user", content);
    }
    
    public static ChatMessage assistant(String content) {
        return new ChatMessage("assistant", content);
    }
    
    // Utility methods
    public boolean isSystemMessage() {
        return "system".equals(role);
    }
    
    public boolean isUserMessage() {
        return "user".equals(role);
    }
    
    public boolean isAssistantMessage() {
        return "assistant".equals(role);
    }
    
    public int getContentLength() {
        return content != null ? content.length() : 0;
    }
    
    // Getters and Setters
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    @Override
    public String toString() {
        return "ChatMessage{" +
                "role='" + role + '\'' +
                ", content='" + (content != null && content.length() > 50 ? 
                               content.substring(0, 50) + "..." : content) + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}