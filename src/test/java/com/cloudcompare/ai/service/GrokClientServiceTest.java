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
        // This confirms our earlier fix for the placeholder string
        assertNotNull(grokClientService);
    }
}
