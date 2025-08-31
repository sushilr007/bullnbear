package quiz.rag;

import java.util.List;
import java.util.Map;

/**
 * Service for retrieving and managing content chunks
 */
public interface ContentRetriever {
    
    /**
     * Retrieve a content chunk by ID
     * @param chunkId Chunk identifier
     * @return Content chunk or null if not found
     */
    ContentChunk getChunk(String chunkId);
    
    /**
     * Retrieve multiple content chunks by IDs
     * @param chunkIds List of chunk identifiers
     * @return List of found content chunks
     */
    List<ContentChunk> getChunks(List<String> chunkIds);
    
    /**
     * Retrieve chunks by subject and topic
     * @param subject Subject filter
     * @param topic Topic filter (optional)
     * @param limit Maximum number of results
     * @return List of matching content chunks
     */
    List<ContentChunk> getChunksBySubjectAndTopic(String subject, String topic, int limit);
    
    /**
     * Search chunks by text content
     * @param query Search query
     * @param limit Maximum number of results
     * @return List of matching content chunks
     */
    List<ContentChunk> searchChunksByContent(String query, int limit);
    
    /**
     * Get related chunks based on similarity
     * @param sourceChunk Reference chunk
     * @param limit Maximum number of results
     * @return List of related chunks
     */
    List<ContentChunk> getRelatedChunks(ContentChunk sourceChunk, int limit);
    
    /**
     * Get chunks from specific document
     * @param documentPath Source document path
     * @param limit Maximum number of results
     * @return List of chunks from the document
     */
    List<ContentChunk> getChunksFromDocument(String documentPath, int limit);
    
    /**
     * Filter chunks by metadata criteria
     * @param filters Map of metadata filters
     * @param limit Maximum number of results
     * @return List of matching chunks
     */
    List<ContentChunk> filterChunksByMetadata(Map<String, Object> filters, int limit);
    
    /**
     * Get chunk statistics
     * @return Map containing retrieval statistics
     */
    Map<String, Object> getRetrievalStatistics();
}