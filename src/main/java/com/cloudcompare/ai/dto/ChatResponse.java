package com.cloudcompare.ai.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Response body for chatbot endpoints.
 */
@Data
@Builder
public class ChatResponse {
    private String reply;
}

