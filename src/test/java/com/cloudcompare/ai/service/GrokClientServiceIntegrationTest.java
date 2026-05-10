package com.cloudcompare.ai.service;

import com.cloudcompare.ai.dto.AiToolResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@ActiveProfiles("test")
class GrokClientServiceIntegrationTest {

    @Autowired
    private GrokClientService grokClientService;

    @Test
    void testFetchComparisonFallback() throws Exception {
        // Since we are in the 'test' profile and GROK_API_KEYS is not set, 
        // it should use the mock fallback.
        List<Map<String, Object>> results = grokClientService.fetchComparisonFromGrok("compute", "t3.medium");
        assertNotNull(results);
        assertEquals(5, results.size());
        assertEquals("AWS", results.get(0).get("provider"));
    }

    @Test
    void testFetchAiToolsFallback() throws Exception {
        List<AiToolResult> results = grokClientService.fetchAiToolsComparisonFromGrok("coding assistant");
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals("ChatGPT", results.get(0).getToolName());
    }

    @Test
    void testExplicitFallbackMethod() {
        List<Map<String, Object>> results = grokClientService.fetchComparisonFallback("compute", "t3.medium", new RuntimeException("Test trip"));
        assertNotNull(results);
        assertEquals(5, results.size());
    }
}
