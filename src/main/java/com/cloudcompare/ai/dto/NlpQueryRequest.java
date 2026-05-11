package com.cloudcompare.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request body for POST /api/nlp-compare
 * Accepts free-form natural language queries instead of predefined categories.
 */
public class NlpQueryRequest {

    @NotBlank(message = "Query is required")
    @Size(max = 500, message = "Query must be under 500 characters")
    private String query;

    // Optional: user can still provide a category hint
    private String categoryHint;

    // Getters and Setters
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getCategoryHint() {
        return categoryHint;
    }

    public void setCategoryHint(String categoryHint) {
        this.categoryHint = categoryHint;
    }
}
