package com.cloudcompare.ai.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request body for POST /api/ai-compare
 */
public class AiCompareRequest {

    // Backward-compatible: existing clients send dropdown purpose.
    @NotBlank(message = "Purpose is required")
    private String purpose;

    // Optional NLP free-text input (if present, it overrides purpose).
    private String queryText;

    // Getters and Setters
    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }
}
