package quiz.rag;

import java.util.List;
import java.util.Map;

/**
 * Interface for processing educational documents and extracting content
 */
public interface DocumentProcessor {
    
    /**
     * Process a document file and extract content chunks
     * @param filePath Path to the document file
     * @param subject Subject classification for the document
     * @return List of processed content chunks
     */
    List<ContentChunk> processDocument(String filePath, String subject);
    
    /**
     * Process raw text content
     * @param text Raw text content
     * @param subject Subject classification
     * @return List of processed content chunks
     */
    List<ContentChunk> processText(String text, String subject);
    
    /**
     * Extract metadata from document
     * @param filePath Path to the document file
     * @return Document metadata
     */
    Map<String, Object> extractMetadata(String filePath);
    
    /**
     * Get supported file formats
     * @return List of supported file extensions
     */
    List<String> getSupportedFormats();
    
    /**
     * Validate if file can be processed
     * @param filePath Path to the file
     * @return true if file can be processed
     */
    boolean canProcess(String filePath);
}