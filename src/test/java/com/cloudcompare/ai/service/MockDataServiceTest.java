package com.cloudcompare.ai.service;

import com.cloudcompare.ai.dto.AiToolResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MockDataServiceTest {

    private MockDataService mockDataService;

    @BeforeEach
    void setUp() {
        mockDataService = new MockDataService();
    }

    @Test
    void testGetMockComparison() {
        List<Map<String, Object>> result = mockDataService.getMockComparison("Database");
        assertNotNull(result);
        assertEquals(5, result.size());
        assertEquals("AWS", result.get(0).get("provider"));
        assertEquals("AWS Database", result.get(0).get("service_name"));
    }

    @Test
    void testGetMockAiToolsForPurpose_EmptyOrNull() {
        List<AiToolResult> resultNull = mockDataService.getMockAiToolsForPurpose(null);
        assertNotNull(resultNull);
        assertEquals(5, resultNull.size());

        List<AiToolResult> resultEmpty = mockDataService.getMockAiToolsForPurpose("");
        assertNotNull(resultEmpty);
        assertEquals(5, resultEmpty.size());
    }

    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.CsvSource({
        "coding, GitHub Copilot",
        "writing, Claude",
        "data analysis, ChatGPT (Code Interpreter)",
        "image generation, Midjourney",
        "video editing, Runway",
        "slide presentation, Gamma",
        "music production, Suno",
        "research tool, ChatGPT",
        "unknown purpose, ChatGPT"
    })
    void testGetMockAiToolsForPurpose(String purpose, String expectedFirstTool) {
        List<AiToolResult> result = mockDataService.getMockAiToolsForPurpose(purpose);
        assertNotNull(result);
        assertEquals(expectedFirstTool, result.get(0).getToolName());
    }
}
