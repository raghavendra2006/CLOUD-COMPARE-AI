package com.cloudcompare.ai.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class GrokClientServiceTest {

    @Autowired
    private GrokClientService grokClientService;

    @Test
    void testPlaceholderDetection() {
        assertNotNull(grokClientService);
    }

    @Test
    void testFetchComparisonFallback() {
        java.util.List<java.util.Map<String, Object>> result = grokClientService.fetchComparisonFallback("compute", "Virtual Machines", new RuntimeException("test error"));
        assertNotNull(result);
        org.junit.jupiter.api.Assertions.assertFalse(result.isEmpty());
    }

    @Test
    void testFetchAiToolsFallback() {
        java.util.List<com.cloudcompare.ai.dto.AiToolResult> result = grokClientService.fetchAiToolsFallback("coding", new RuntimeException("test error"));
        assertNotNull(result);
        org.junit.jupiter.api.Assertions.assertFalse(result.isEmpty());
    }

    @Test
    void testFetchComparisonFromGrokPlaceholder() throws java.io.IOException, InterruptedException {
        // Will use mock data since test profile has no real keys
        java.util.List<java.util.Map<String, Object>> result = grokClientService.fetchComparisonFromGrok("compute", "Virtual Machines");
        assertNotNull(result);
        org.junit.jupiter.api.Assertions.assertFalse(result.isEmpty());
    }

    @Test
    void testFetchAiToolsComparisonFromGrokPlaceholder() throws java.io.IOException, InterruptedException {
        // Will use mock data since test profile has no real keys
        java.util.List<com.cloudcompare.ai.dto.AiToolResult> result = grokClientService.fetchAiToolsComparisonFromGrok("coding");
        assertNotNull(result);
        org.junit.jupiter.api.Assertions.assertFalse(result.isEmpty());
    }
}
