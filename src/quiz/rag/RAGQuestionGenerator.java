package quiz.rag;

import quiz.core.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * RAG-based question generator that uses document retrieval
 * to generate contextual questions from educational content
 */
public class RAGQuestionGenerator implements QuestionGenerator {
    
    private final DocumentProcessor documentProcessor;
    private final VectorStore vectorStore;
    private final EmbeddingService embeddingService;
    private final ContentRetriever contentRetriever;
    private final QuestionTemplateEngine templateEngine;
    
    public RAGQuestionGenerator(DocumentProcessor documentProcessor,
                               VectorStore vectorStore,
                               EmbeddingService embeddingService,
                               ContentRetriever contentRetriever,
                               QuestionTemplateEngine templateEngine) {
        this.documentProcessor = documentProcessor;
        this.vectorStore = vectorStore;
        this.embeddingService = embeddingService;
        this.contentRetriever = contentRetriever;
        this.templateEngine = templateEngine;
    }
    
    @Override
    public List<Question> generateQuestions(QuizConfig config) {
        List<Question> questions = new ArrayList<>();
        
        try {
            // Retrieve relevant content based on subject and topic
            List<ContentChunk> relevantContent = retrieveRelevantContent(config);
            
            if (relevantContent.isEmpty()) {
                throw new RuntimeException("No relevant content found for: " + 
                                         config.getSubject() + " - " + config.getTopic());
            }
            
            // Distribute questions across content chunks
            int questionsPerChunk = Math.max(1, config.getQuestionCount() / relevantContent.size());
            int remainingQuestions = config.getQuestionCount();
            
            for (ContentChunk chunk : relevantContent) {
                if (remainingQuestions <= 0) break;
                
                int questionsToGenerate = Math.min(questionsPerChunk, remainingQuestions);
                List<Question> chunkQuestions = generateQuestionsFromChunk(chunk, config, questionsToGenerate);
                questions.addAll(chunkQuestions);
                remainingQuestions -= chunkQuestions.size();
            }
            
            // Fill remaining questions if needed
            if (remainingQuestions > 0) {
                List<Question> additionalQuestions = generateAdditionalQuestions(
                    relevantContent, config, remainingQuestions);
                questions.addAll(additionalQuestions);
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate RAG questions", e);
        }
        
        return questions.stream()
                       .limit(config.getQuestionCount())
                       .collect(Collectors.toList());
    }
    
    @Override
    public List<Question> generateFromContent(String content, QuizConfig config) {
        try {
            // Process the provided content
            List<ContentChunk> chunks = documentProcessor.processText(content, config.getSubject());
            
            List<Question> questions = new ArrayList<>();
            int questionsPerChunk = Math.max(1, config.getQuestionCount() / chunks.size());
            
            for (ContentChunk chunk : chunks) {
                List<Question> chunkQuestions = generateQuestionsFromChunk(chunk, config, questionsPerChunk);
                questions.addAll(chunkQuestions);
                
                if (questions.size() >= config.getQuestionCount()) {
                    break;
                }
            }
            
            return questions.stream()
                           .limit(config.getQuestionCount())
                           .collect(Collectors.toList());
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate questions from content", e);
        }
    }
    
    @Override
    public boolean supports(QuizConfig config) {
        return config.getPreferredGenerator() == GeneratorType.RAG ||
               config.getPreferredGenerator() == GeneratorType.HYBRID;
    }
    
    @Override
    public GeneratorType getType() {
        return GeneratorType.RAG;
    }
    
    @Override
    public List<QuestionType> getSupportedTypes() {
        return Arrays.asList(
            QuestionType.MULTIPLE_CHOICE,
            QuestionType.TRUE_FALSE,
            QuestionType.SHORT_ANSWER,
            QuestionType.FILL_IN_THE_BLANK
        );
    }
    
    /**
     * Retrieve relevant content chunks based on quiz configuration
     */
    private List<ContentChunk> retrieveRelevantContent(QuizConfig config) {
        String query = buildSearchQuery(config);
        
        // Generate embedding for the query
        float[] queryEmbedding = embeddingService.generateEmbedding(query);
        
        // Search vector store for similar content
        List<VectorSearchResult> searchResults = vectorStore.search(
            queryEmbedding, 
            config.getQuestionCount() * 2, // Get more results for variety
            buildSearchFilters(config)
        );
        
        // Convert search results to content chunks
        return searchResults.stream()
                           .map(result -> contentRetriever.getChunk(result.getId()))
                           .filter(Objects::nonNull)
                           .collect(Collectors.toList());
    }
    
    /**
     * Generate questions from a specific content chunk
     */
    private List<Question> generateQuestionsFromChunk(ContentChunk chunk, QuizConfig config, int count) {
        List<Question> questions = new ArrayList<>();
        
        // Determine question types to generate
        List<QuestionType> typesToGenerate = determineQuestionTypes(config, count);
        
        for (QuestionType type : typesToGenerate) {
            try {
                Question question = generateQuestionOfType(chunk, type, config);
                if (question != null) {
                    questions.add(question);
                }
            } catch (Exception e) {
                // Log error and continue with other questions
                System.err.println("Failed to generate " + type + " question: " + e.getMessage());
            }
        }
        
        return questions;
    }
    
    /**
     * Generate a specific type of question from content chunk
     */
    private Question generateQuestionOfType(ContentChunk chunk, QuestionType type, QuizConfig config) {
        switch (type) {
            case MULTIPLE_CHOICE:
                return generateMultipleChoiceQuestion(chunk, config);
            case TRUE_FALSE:
                return generateTrueFalseQuestion(chunk, config);
            case SHORT_ANSWER:
                return generateShortAnswerQuestion(chunk, config);
            case FILL_IN_THE_BLANK:
                return generateFillInTheBlankQuestion(chunk, config);
            default:
                return null;
        }
    }
    
    /**
     * Generate multiple choice question from content
     */
    private Question generateMultipleChoiceQuestion(ContentChunk chunk, QuizConfig config) {
        // Extract key facts and concepts from chunk
        List<String> keyFacts = extractKeyFacts(chunk);
        if (keyFacts.isEmpty()) return null;
        
        String selectedFact = keyFacts.get(new Random().nextInt(keyFacts.size()));
        
        // Generate question stem
        String questionText = templateEngine.generateQuestionStem(selectedFact, QuestionType.MULTIPLE_CHOICE);
        
        // Generate correct answer and distractors
        String correctAnswer = extractCorrectAnswer(selectedFact);
        List<String> distractors = generateDistractors(correctAnswer, chunk, 3);
        
        // Combine and shuffle options
        List<String> options = new ArrayList<>();
        options.add(correctAnswer);
        options.addAll(distractors);
        Collections.shuffle(options);
        
        return new Question.Builder(questionText, QuestionType.MULTIPLE_CHOICE)
                .withOptions(options)
                .withCorrectAnswer(correctAnswer)
                .withSubject(config.getSubject())
                .withTopic(config.getTopic())
                .withDifficulty(config.getDifficulty())
                .withSourceContent(chunk.getContent())
                .withPoints(1.0)
                .build();
    }
    
    /**
     * Generate true/false question from content
     */
    private Question generateTrueFalseQuestion(ContentChunk chunk, QuizConfig config) {
        List<String> statements = extractStatements(chunk);
        if (statements.isEmpty()) return null;
        
        String statement = statements.get(new Random().nextInt(statements.size()));
        boolean isTrue = new Random().nextBoolean();
        
        String questionText;
        String correctAnswer;
        
        if (isTrue) {
            questionText = statement;
            correctAnswer = "True";
        } else {
            questionText = negateStatement(statement);
            correctAnswer = "False";
        }
        
        return new Question.Builder(questionText, QuestionType.TRUE_FALSE)
                .withOptions(Arrays.asList("True", "False"))
                .withCorrectAnswer(correctAnswer)
                .withSubject(config.getSubject())
                .withTopic(config.getTopic())
                .withDifficulty(config.getDifficulty())
                .withSourceContent(chunk.getContent())
                .withPoints(1.0)
                .build();
    }
    
    /**
     * Generate short answer question from content
     */
    private Question generateShortAnswerQuestion(ContentChunk chunk, QuizConfig config) {
        List<String> concepts = extractKeyConcepts(chunk);
        if (concepts.isEmpty()) return null;
        
        String concept = concepts.get(new Random().nextInt(concepts.size()));
        String questionText = templateEngine.generateQuestionStem(concept, QuestionType.SHORT_ANSWER);
        String answer = extractConceptDefinition(concept, chunk);
        
        return new Question.Builder(questionText, QuestionType.SHORT_ANSWER)
                .withCorrectAnswer(answer)
                .withSubject(config.getSubject())
                .withTopic(config.getTopic())
                .withDifficulty(config.getDifficulty())
                .withSourceContent(chunk.getContent())
                .withPoints(2.0)
                .build();
    }
    
    /**
     * Generate fill-in-the-blank question from content
     */
    private Question generateFillInTheBlankQuestion(ContentChunk chunk, QuizConfig config) {
        List<String> sentences = extractSentences(chunk);
        if (sentences.isEmpty()) return null;
        
        String sentence = sentences.get(new Random().nextInt(sentences.size()));
        String[] words = sentence.split("\\s+");
        
        if (words.length < 5) return null; // Too short for blanks
        
        // Select important words to blank out
        List<String> importantWords = extractImportantWords(words);
        if (importantWords.isEmpty()) return null;
        
        String wordToBlank = importantWords.get(new Random().nextInt(importantWords.size()));
        String questionText = sentence.replace(wordToBlank, "_____");
        
        return new Question.Builder(questionText, QuestionType.FILL_IN_THE_BLANK)
                .withCorrectAnswer(wordToBlank)
                .withSubject(config.getSubject())
                .withTopic(config.getTopic())
                .withDifficulty(config.getDifficulty())
                .withSourceContent(chunk.getContent())
                .withPoints(1.5)
                .build();
    }
    
    // Helper methods (simplified implementations)
    private String buildSearchQuery(QuizConfig config) {
        StringBuilder query = new StringBuilder();
        query.append(config.getSubject());
        if (config.getTopic() != null) {
            query.append(" ").append(config.getTopic());
        }
        return query.toString();
    }
    
    private Map<String, Object> buildSearchFilters(QuizConfig config) {
        Map<String, Object> filters = new HashMap<>();
        filters.put("subject", config.getSubject());
        filters.put("level", config.getLevel().name());
        return filters;
    }
    
    private List<QuestionType> determineQuestionTypes(QuizConfig config, int count) {
        List<QuestionType> availableTypes = config.getQuestionTypes() != null ? 
            config.getQuestionTypes() : getSupportedTypes();
        
        List<QuestionType> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(availableTypes.get(i % availableTypes.size()));
        }
        return result;
    }
    
    private List<Question> generateAdditionalQuestions(List<ContentChunk> content, 
                                                      QuizConfig config, 
                                                      int count) {
        List<Question> questions = new ArrayList<>();
        
        for (int i = 0; i < count && i < content.size(); i++) {
            ContentChunk chunk = content.get(i % content.size());
            List<Question> chunkQuestions = generateQuestionsFromChunk(chunk, config, 1);
            questions.addAll(chunkQuestions);
        }
        
        return questions;
    }
    
    // Placeholder methods for content analysis (would be implemented with NLP libraries)
    private List<String> extractKeyFacts(ContentChunk chunk) {
        // Implementation would use NLP to extract factual statements
        return Arrays.asList(chunk.getContent().split("\\. "));
    }
    
    private String extractCorrectAnswer(String fact) {
        // Simple extraction - would be more sophisticated in real implementation
        String[] parts = fact.split(" is | are | was | were ");
        return parts.length > 1 ? parts[1].trim() : "Unknown";
    }
    
    private List<String> generateDistractors(String correctAnswer, ContentChunk chunk, int count) {
        // Generate plausible wrong answers
        return Arrays.asList("Option A", "Option B", "Option C");
    }
    
    private List<String> extractStatements(ContentChunk chunk) {
        return Arrays.asList(chunk.getContent().split("\\. "));
    }
    
    private String negateStatement(String statement) {
        // Simple negation - would be more sophisticated
        if (statement.toLowerCase().contains(" is ")) {
            return statement.replace(" is ", " is not ");
        }
        return "It is not true that " + statement.toLowerCase();
    }
    
    private List<String> extractKeyConcepts(ContentChunk chunk) {
        // Extract important concepts/terms
        return Arrays.asList("concept1", "concept2", "concept3");
    }
    
    private String extractConceptDefinition(String concept, ContentChunk chunk) {
        // Extract definition of concept from chunk
        return "Definition of " + concept;
    }
    
    private List<String> extractSentences(ContentChunk chunk) {
        return Arrays.asList(chunk.getContent().split("\\. "));
    }
    
    private List<String> extractImportantWords(String[] words) {
        // Filter for important words (nouns, verbs, adjectives)
        return Arrays.stream(words)
                     .filter(word -> word.length() > 3)
                     .filter(word -> !isStopWord(word))
                     .collect(Collectors.toList());
    }
    
    private boolean isStopWord(String word) {
        Set<String> stopWords = Set.of("the", "and", "or", "but", "in", "on", "at", "to", "for");
        return stopWords.contains(word.toLowerCase());
    }
}