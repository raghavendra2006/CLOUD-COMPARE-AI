package com.cloudcompare.ai.service;

import com.cloudcompare.ai.dto.AiToolResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GrokClientServiceUnitTest {

    private GrokClientService grokClientService;

    @Mock
    private MockDataService mockDataService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        grokClientService = new GrokClientService(mockDataService);
        ReflectionTestUtils.setField(grokClientService, "apiKeysRaw", "key1, key2");
        ReflectionTestUtils.setField(grokClientService, "endpoint", "http://test.com");
        ReflectionTestUtils.setField(grokClientService, "model", "test-model");
        ReflectionTestUtils.setField(grokClientService, "timeoutMs", 1000);
    }

    @Test
    void testGetNextApiKeyRotation() {
        String key1 = (String) ReflectionTestUtils.invokeMethod(grokClientService, "getNextApiKey");
        String key2 = (String) ReflectionTestUtils.invokeMethod(grokClientService, "getNextApiKey");
        String key3 = (String) ReflectionTestUtils.invokeMethod(grokClientService, "getNextApiKey");

        assertEquals("key1", key1);
        assertEquals("key2", key2);
        assertEquals("key1", key3);
    }

    @Test
    void testGetNextApiKeyEmpty() {
        ReflectionTestUtils.setField(grokClientService, "apiKeysRaw", "");
        String key = (String) ReflectionTestUtils.invokeMethod(grokClientService, "getNextApiKey");
        assertEquals("YOUR_GROQ_API_KEYS_HERE", key);
    }

    @Test
    void testExtractJsonScenarios() {
        String raw = "Some text [{\"id\":1}] more text";
        String extracted = (String) ReflectionTestUtils.invokeMethod(grokClientService, "extractJson", raw);
        assertEquals("[{\"id\":1}]", extracted);

        raw = "No brackets here";
        extracted = (String) ReflectionTestUtils.invokeMethod(grokClientService, "extractJson", raw);
        assertEquals("No brackets here", extracted);

        raw = "Braces only {\"id\":1}";
        extracted = (String) ReflectionTestUtils.invokeMethod(grokClientService, "extractJson", raw);
        assertEquals("{\"id\":1}", extracted);

        raw = null;
        extracted = (String) ReflectionTestUtils.invokeMethod(grokClientService, "extractJson", raw);
        assertEquals("[]", extracted);
    }
}
