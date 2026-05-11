package com.cloudcompare.ai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

/**
 * Request body for chatbot endpoints.
 */
@Data
public class ChatRequest {

    @NotBlank(message = "Question is required")
    private String question;

    /**
     * Optional context from frontend for cloud compare.
     */
    private Map<String, Object> cloudContext;

    /**
     * Optional context from frontend for AI tools compare.
     */
    private Map<String, Object> aiToolsContext;
}

