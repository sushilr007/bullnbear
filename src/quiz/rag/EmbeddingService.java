package quiz.rag;

import java.util.List;

/**
 * Service for generating text embeddings for vector similarity search
 */
public interface EmbeddingService {
    
    /**
     * Generate embedding for a single text
     * @param text Input text
     * @return Vector embedding
     */
    float[] generateEmbedding(String text);
    
    /**
     * Generate embeddings for multiple texts
     * @param texts List of input texts
     * @return List of vector embeddings
     */
    List<float[]> generateEmbeddings(List<String> texts);
    
    /**
     * Get the dimension of embeddings produced by this service
     * @return Embedding dimension
     */
    int getEmbeddingDimension();
    
    /**
     * Calculate cosine similarity between two embeddings
     * @param embedding1 First embedding
     * @param embedding2 Second embedding
     * @return Similarity score between -1 and 1
     */
    double calculateSimilarity(float[] embedding1, float[] embedding2);
    
    /**
     * Get the model name used for embeddings
     * @return Model identifier
     */
    String getModelName();
    
    /**
     * Check if the service is available and operational
     * @return true if service is ready
     */
    boolean isAvailable();
    
    /**
     * Get maximum text length supported
     * @return Maximum characters or tokens
     */
    int getMaxTextLength();
}