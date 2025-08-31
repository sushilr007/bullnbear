package quiz.llm;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents the result of question validation
 */
public class ValidationResult {
    private boolean valid;
    private double qualityScore;
    private List<String> issues;
    private List<String> suggestions;
    private String summary;
    
    // Constructors
    public ValidationResult() {
        this.issues = new ArrayList<>();
        this.suggestions = new ArrayList<>();
        this.valid = true;
        this.qualityScore = 100.0;
    }
    
    public ValidationResult(boolean valid) {
        this();
        this.valid = valid;
    }
    
    // Builder pattern
    public static class Builder {
        private ValidationResult result;
        
        public Builder() {
            result = new ValidationResult();
        }
        
        public Builder valid(boolean valid) {
            result.valid = valid;
            return this;
        }
        
        public Builder withQualityScore(double score) {
            result.qualityScore = score;
            return this;
        }
        
        public Builder addIssue(String issue) {
            result.issues.add(issue);
            result.valid = false; // Any issue makes it invalid
            return this;
        }
        
        public Builder addSuggestion(String suggestion) {
            result.suggestions.add(suggestion);
            return this;
        }
        
        public Builder withSummary(String summary) {
            result.summary = summary;
            return this;
        }
        
        public ValidationResult build() {
            if (result.summary == null) {
                result.summary = generateSummary();
            }
            return result;
        }
        
        private String generateSummary() {
            if (result.valid) {
                return "Question validation passed with quality score: " + 
                       String.format("%.1f", result.qualityScore);
            } else {
                return "Question validation failed with " + result.issues.size() + " issues";
            }
        }
    }
    
    // Utility methods
    public boolean hasIssues() {
        return !issues.isEmpty();
    }
    
    public boolean hasSuggestions() {
        return !suggestions.isEmpty();
    }
    
    public boolean isHighQuality() {
        return valid && qualityScore >= 80.0;
    }
    
    public String getQualityLevel() {
        if (!valid) return "Invalid";
        if (qualityScore >= 90) return "Excellent";
        if (qualityScore >= 80) return "Good";
        if (qualityScore >= 70) return "Fair";
        if (qualityScore >= 60) return "Poor";
        return "Very Poor";
    }
    
    public String getDetailedReport() {
        StringBuilder report = new StringBuilder();
        report.append("Validation Summary: ").append(summary).append("\n");
        report.append("Valid: ").append(valid).append("\n");
        report.append("Quality Score: ").append(String.format("%.1f", qualityScore)).append("\n");
        
        if (hasIssues()) {
            report.append("\nIssues:\n");
            for (int i = 0; i < issues.size(); i++) {
                report.append("  ").append(i + 1).append(". ").append(issues.get(i)).append("\n");
            }
        }
        
        if (hasSuggestions()) {
            report.append("\nSuggestions:\n");
            for (int i = 0; i < suggestions.size(); i++) {
                report.append("  ").append(i + 1).append(". ").append(suggestions.get(i)).append("\n");
            }
        }
        
        return report.toString();
    }
    
    // Getters and Setters
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    
    public double getQualityScore() { return qualityScore; }
    public void setQualityScore(double qualityScore) { this.qualityScore = qualityScore; }
    
    public List<String> getIssues() { return issues; }
    public void setIssues(List<String> issues) { this.issues = issues; }
    
    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
    
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    
    @Override
    public String toString() {
        return "ValidationResult{" +
                "valid=" + valid +
                ", qualityScore=" + qualityScore +
                ", issueCount=" + issues.size() +
                ", suggestionCount=" + suggestions.size() +
                '}';
    }
}