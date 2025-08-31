package quiz.config;

import java.util.List;
import java.util.ArrayList;

/**
 * Result of configuration validation
 */
public class ConfigValidationResult {
    private boolean valid;
    private List<String> errors;
    private List<String> warnings;
    private List<String> suggestions;
    private String summary;
    
    // Constructors
    public ConfigValidationResult() {
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
        this.suggestions = new ArrayList<>();
        this.valid = true;
    }
    
    public ConfigValidationResult(boolean valid) {
        this();
        this.valid = valid;
    }
    
    // Builder pattern
    public static class Builder {
        private ConfigValidationResult result;
        
        public Builder() {
            result = new ConfigValidationResult();
        }
        
        public Builder valid(boolean valid) {
            result.valid = valid;
            return this;
        }
        
        public Builder addError(String error) {
            result.errors.add(error);
            result.valid = false;
            return this;
        }
        
        public Builder addWarning(String warning) {
            result.warnings.add(warning);
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
        
        public ConfigValidationResult build() {
            if (result.summary == null) {
                result.summary = generateSummary();
            }
            return result;
        }
        
        private String generateSummary() {
            if (result.valid) {
                return "Configuration is valid" + 
                       (result.warnings.isEmpty() ? "" : " with " + result.warnings.size() + " warnings");
            } else {
                return "Configuration is invalid with " + result.errors.size() + " errors";
            }
        }
    }
    
    // Utility methods
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
    
    public boolean hasSuggestions() {
        return !suggestions.isEmpty();
    }
    
    public String getDetailedReport() {
        StringBuilder report = new StringBuilder();
        report.append("Configuration Validation Report\n");
        report.append("==============================\n");
        report.append("Status: ").append(valid ? "VALID" : "INVALID").append("\n");
        report.append("Summary: ").append(summary).append("\n\n");
        
        if (hasErrors()) {
            report.append("Errors:\n");
            for (int i = 0; i < errors.size(); i++) {
                report.append("  ").append(i + 1).append(". ").append(errors.get(i)).append("\n");
            }
            report.append("\n");
        }
        
        if (hasWarnings()) {
            report.append("Warnings:\n");
            for (int i = 0; i < warnings.size(); i++) {
                report.append("  ").append(i + 1).append(". ").append(warnings.get(i)).append("\n");
            }
            report.append("\n");
        }
        
        if (hasSuggestions()) {
            report.append("Suggestions:\n");
            for (int i = 0; i < suggestions.size(); i++) {
                report.append("  ").append(i + 1).append(". ").append(suggestions.get(i)).append("\n");
            }
        }
        
        return report.toString();
    }
    
    // Getters and Setters
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    
    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
    
    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }
    
    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
    
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    
    @Override
    public String toString() {
        return "ConfigValidationResult{" +
                "valid=" + valid +
                ", errorCount=" + errors.size() +
                ", warningCount=" + warnings.size() +
                ", summary='" + summary + '\'' +
                '}';
    }
}