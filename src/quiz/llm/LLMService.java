package quiz.llm;

import java.util.List;
import java.util.Map;

/**
 * Interface for interacting with Large Language Models
 */
public interface LLMService {
    
    /**
     * Generate text using the LLM
     * @param prompt Input prompt
     * @param settings Generation settings (temperature, max_tokens, etc.)
     * @return LLM response
     */
    LLMResponse generateText(String prompt, Map<String, Object> settings);
    
    /**
     * Generate text with default settings
     * @param prompt Input prompt
     * @return LLM response
     */
    LLMResponse generateText(String prompt);
    
    /**
     * Generate multiple responses for variety
     * @param prompt Input prompt
     * @param count Number of responses to generate
     * @param settings Generation settings
     * @return List of LLM responses
     */
    List<LLMResponse> generateMultiple(String prompt, int count, Map<String, Object> settings);
    
    /**
     * Chat-style interaction for contextual generation
     * @param messages List of chat messages
     * @param settings Generation settings
     * @return LLM response
     */
    LLMResponse chat(List<ChatMessage> messages, Map<String, Object> settings);
    
    /**
     * Check if the service is available
     * @return true if service is operational
     */
    boolean isAvailable();
    
    /**
     * Get the model name/identifier
     * @return Model identifier
     */
    String getModelName();
    
    /**
     * Get model capabilities
     * @return Map of model capabilities and limits
     */
    Map<String, Object> getModelCapabilities();
    
    /**
     * Estimate token count for text
     * @param text Input text
     * @return Estimated token count
     */
    int estimateTokenCount(String text);
    
    /**
     * Get maximum context length
     * @return Maximum tokens in context
     */
    int getMaxContextLength();
    
    /**
     * Validate prompt length
     * @param prompt Input prompt
     * @return true if prompt is within limits
     */
    boolean isValidPromptLength(String prompt);
}