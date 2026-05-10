package com.cloudcompare.ai.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @Test
    void testExtractJson() throws Exception {
        java.lang.reflect.Method method = GrokClientService.class.getDeclaredMethod("extractJson", String.class);
        method.setAccessible(true);
        
        // Test null/empty
        assertEquals("[]", method.invoke(grokClientService, (String) null));
        assertEquals("[]", method.invoke(grokClientService, ""));
        
        // Test no brackets
        assertEquals("raw data", method.invoke(grokClientService, "raw data"));
        
        // Test with brackets
        assertEquals("[{}]", method.invoke(grokClientService, "some text [{}] more text"));
        assertEquals("{}", method.invoke(grokClientService, "text {}"));
        
        // Test edge cases
        assertEquals("invalid [ text", method.invoke(grokClientService, "invalid [ text"));
    }

    @Test
    void testGetApiKeys() throws Exception {
        java.lang.reflect.Method method = GrokClientService.class.getDeclaredMethod("getApiKeys");
        method.setAccessible(true);
        
        @SuppressWarnings("unchecked")
        java.util.List<String> keys = (java.util.List<String>) method.invoke(grokClientService);
        assertNotNull(keys);
    }
}
