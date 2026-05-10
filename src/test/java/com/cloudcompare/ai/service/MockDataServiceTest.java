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

    @Test
    void testGetMockAiToolsForPurpose_Coding() {
        List<AiToolResult> result = mockDataService.getMockAiToolsForPurpose("coding");
        assertNotNull(result);
        assertEquals("GitHub Copilot", result.get(0).getToolName());
    }

    @Test
    void testGetMockAiToolsForPurpose_Writing() {
        List<AiToolResult> result = mockDataService.getMockAiToolsForPurpose("writing");
        assertNotNull(result);
        assertEquals("Claude", result.get(0).getToolName());
    }

    @Test
    void testGetMockAiToolsForPurpose_Data() {
        List<AiToolResult> result = mockDataService.getMockAiToolsForPurpose("data analysis");
        assertNotNull(result);
        assertEquals("ChatGPT (Code Interpreter)", result.get(0).getToolName());
    }

    @Test
    void testGetMockAiToolsForPurpose_Image() {
        List<AiToolResult> result = mockDataService.getMockAiToolsForPurpose("image generation");
        assertNotNull(result);
        assertEquals("Midjourney", result.get(0).getToolName());
    }

    @Test
    void testGetMockAiToolsForPurpose_Video() {
        List<AiToolResult> result = mockDataService.getMockAiToolsForPurpose("video editing");
        assertNotNull(result);
        assertEquals("Runway", result.get(0).getToolName());
    }

    @Test
    void testGetMockAiToolsForPurpose_Presentation() {
        List<AiToolResult> result = mockDataService.getMockAiToolsForPurpose("slide presentation");
        assertNotNull(result);
        assertEquals("Gamma", result.get(0).getToolName());
    }

    @Test
    void testGetMockAiToolsForPurpose_Music() {
        List<AiToolResult> result = mockDataService.getMockAiToolsForPurpose("music production");
        assertNotNull(result);
        assertEquals("Suno", result.get(0).getToolName());
    }

    @Test
    void testGetMockAiToolsForPurpose_Research() {
        List<AiToolResult> result = mockDataService.getMockAiToolsForPurpose("research tool");
        assertNotNull(result);
        assertEquals("ChatGPT", result.get(0).getToolName());
    }

    @Test
    void testGetMockAiToolsForPurpose_Default() {
        List<AiToolResult> result = mockDataService.getMockAiToolsForPurpose("unknown purpose");
        assertNotNull(result);
        assertEquals("ChatGPT", result.get(0).getToolName());
    }
}
