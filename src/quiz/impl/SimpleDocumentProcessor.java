package quiz.impl;

import quiz.rag.*;
import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Simple implementation of DocumentProcessor for educational content
 */
public class SimpleDocumentProcessor implements DocumentProcessor {
    
    private static final int DEFAULT_CHUNK_SIZE = 500; // words
    private static final int CHUNK_OVERLAP = 50; // words
    
    @Override
    public List<ContentChunk> processDocument(String filePath, String subject) {
        try {
            String content = Files.readString(Paths.get(filePath));
            return processText(content, subject);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read document: " + filePath, e);
        }
    }
    
    @Override
    public List<ContentChunk> processText(String text, String subject) {
        List<ContentChunk> chunks = new ArrayList<>();
        
        try {
            // Clean and normalize text
            String cleanedText = cleanText(text);
            
            // Split into sentences for better chunking
            List<String> sentences = splitIntoSentences(cleanedText);
            
            // Create chunks with overlap
            List<String> chunkTexts = createChunks(sentences, DEFAULT_CHUNK_SIZE, CHUNK_OVERLAP);
            
            // Convert to ContentChunk objects
            for (int i = 0; i < chunkTexts.size(); i++) {
                String chunkText = chunkTexts.get(i);
                
                ContentChunk chunk = new ContentChunk.Builder(chunkText)
                        .withSubject(subject)
                        .withChunkIndex(i)
                        .withMetadata("word_count", countWords(chunkText))
                        .withMetadata("sentence_count", countSentences(chunkText))
                        .withMetadata("processed_at", System.currentTimeMillis())
                        .build();
                
                chunks.add(chunk);
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to process text", e);
        }
        
        return chunks;
    }
    
    @Override
    public Map<String, Object> extractMetadata(String filePath) {
        Map<String, Object> metadata = new HashMap<>();
        
        try {
            File file = new File(filePath);
            metadata.put("file_name", file.getName());
            metadata.put("file_size", file.length());
            metadata.put("last_modified", file.lastModified());
            metadata.put("file_extension", getFileExtension(filePath));
            
            // Extract content-based metadata
            String content = Files.readString(Paths.get(filePath));
            metadata.put("character_count", content.length());
            metadata.put("word_count", countWords(content));
            metadata.put("line_count", content.split("\n").length);
            
        } catch (Exception e) {
            System.err.println("Failed to extract metadata: " + e.getMessage());
        }
        
        return metadata;
    }
    
    @Override
    public List<String> getSupportedFormats() {
        return Arrays.asList(".txt", ".md", ".pdf", ".docx", ".html");
    }
    
    @Override
    public boolean canProcess(String filePath) {
        String extension = getFileExtension(filePath).toLowerCase();
        return getSupportedFormats().contains(extension);
    }
    
    // Helper methods
    private String cleanText(String text) {
        if (text == null) return "";
        
        return text
                .replaceAll("\\s+", " ") // Normalize whitespace
                .replaceAll("[\\x00-\\x1F\\x7F]", "") // Remove control characters
                .trim();
    }
    
    private List<String> splitIntoSentences(String text) {
        // Simple sentence splitting - could be enhanced with NLP library
        String[] sentences = text.split("(?<=[.!?])\\s+");
        return Arrays.asList(sentences);
    }
    
    private List<String> createChunks(List<String> sentences, int targetChunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();
        List<String> currentChunk = new ArrayList<>();
        int currentWordCount = 0;
        
        for (String sentence : sentences) {
            int sentenceWordCount = countWords(sentence);
            
            // Check if adding this sentence would exceed chunk size
            if (currentWordCount + sentenceWordCount > targetChunkSize && !currentChunk.isEmpty()) {
                // Create chunk from current sentences
                chunks.add(String.join(" ", currentChunk));
                
                // Start new chunk with overlap
                List<String> overlapSentences = getLastSentences(currentChunk, overlap);
                currentChunk = new ArrayList<>(overlapSentences);
                currentWordCount = countWords(String.join(" ", overlapSentences));
            }
            
            currentChunk.add(sentence);
            currentWordCount += sentenceWordCount;
        }
        
        // Add final chunk if not empty
        if (!currentChunk.isEmpty()) {
            chunks.add(String.join(" ", currentChunk));
        }
        
        return chunks;
    }
    
    private List<String> getLastSentences(List<String> sentences, int maxWords) {
        List<String> lastSentences = new ArrayList<>();
        int wordCount = 0;
        
        for (int i = sentences.size() - 1; i >= 0; i--) {
            String sentence = sentences.get(i);
            int sentenceWords = countWords(sentence);
            
            if (wordCount + sentenceWords <= maxWords) {
                lastSentences.add(0, sentence);
                wordCount += sentenceWords;
            } else {
                break;
            }
        }
        
        return lastSentences;
    }
    
    private int countWords(String text) {
        if (text == null || text.trim().isEmpty()) return 0;
        return text.trim().split("\\s+").length;
    }
    
    private int countSentences(String text) {
        if (text == null || text.trim().isEmpty()) return 0;
        return text.split("(?<=[.!?])\\s+").length;
    }
    
    private String getFileExtension(String filePath) {
        int lastDot = filePath.lastIndexOf('.');
        return lastDot > 0 ? filePath.substring(lastDot) : "";
    }
}