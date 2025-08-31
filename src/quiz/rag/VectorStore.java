package quiz.rag;

import java.util.List;
import java.util.Map;

/**
 * Interface for vector storage and similarity search operations
 */
public interface VectorStore {
    
    /**
     * Store a content chunk with its embedding
     * @param chunk Content chunk to store
     * @param embedding Vector embedding of the content
     * @return Storage operation result
     */
    boolean store(ContentChunk chunk, float[] embedding);
    
    /**
     * Store multiple content chunks with their embeddings
     * @param chunks List of content chunks with embeddings
     * @return Number of successfully stored chunks
     */
    int storeBatch(List<ContentChunk> chunks);
    
    /**
     * Search for similar content using vector similarity
     * @param queryEmbedding Query vector
     * @param limit Maximum number of results
     * @param filters Additional filters to apply
     * @return List of search results ordered by similarity
     */
    List<VectorSearchResult> search(float[] queryEmbedding, int limit, Map<String, Object> filters);
    
    /**
     * Search for similar content using text query
     * @param query Text query
     * @param limit Maximum number of results
     * @param filters Additional filters to apply
     * @return List of search results
     */
    List<VectorSearchResult> searchByText(String query, int limit, Map<String, Object> filters);
    
    /**
     * Get a specific content chunk by ID
     * @param chunkId Chunk identifier
     * @return Content chunk or null if not found
     */
    ContentChunk getChunk(String chunkId);
    
    /**
     * Update an existing content chunk
     * @param chunk Updated content chunk
     * @return true if update was successful
     */
    boolean update(ContentChunk chunk);
    
    /**
     * Delete a content chunk
     * @param chunkId Chunk identifier
     * @return true if deletion was successful
     */
    boolean delete(String chunkId);
    
    /**
     * Get total number of stored chunks
     * @return Total chunk count
     */
    long getTotalCount();
    
    /**
     * Get chunks by subject
     * @param subject Subject filter
     * @param limit Maximum number of results
     * @return List of chunks for the subject
     */
    List<ContentChunk> getChunksBySubject(String subject, int limit);
    
    /**
     * Get chunks by topic
     * @param topic Topic filter
     * @param limit Maximum number of results
     * @return List of chunks for the topic
     */
    List<ContentChunk> getChunksByTopic(String topic, int limit);
    
    /**
     * Clear all stored data
     * @return true if clear operation was successful
     */
    boolean clear();
    
    /**
     * Check if the vector store is healthy and operational
     * @return true if operational
     */
    boolean isHealthy();
    
    /**
     * Get storage statistics
     * @return Map containing storage statistics
     */
    Map<String, Object> getStatistics();
}