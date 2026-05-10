package com.cloudcompare.ai.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class DtoTest {

    @Test
    void testAiCompareRequest() {
        AiCompareRequest request = new AiCompareRequest();
        request.setPurpose("Test Purpose");
        
        assertEquals("Test Purpose", request.getPurpose());
    }

    @Test
    void testApiResponse() {
        ApiResponse<String> success = ApiResponse.success("Data", "Success message");
        assertTrue(success.isSuccess());
        assertEquals("Data", success.getData());
        assertEquals("Success message", success.getMessage());

        ApiResponse<Void> error = ApiResponse.error("Error message");
        assertFalse(error.isSuccess());
        assertEquals("Error message", error.getError());
    }

    @Test
    void testAiToolResult() {
        AiToolResult result = new AiToolResult();
        result.setRank(1);
        result.setToolName("TestTool");
        result.setProvider("TestProvider");
        result.setModelNumber("v1");
        result.setScore(9.5);
        result.setPricing("$10");
        result.setDescription("Desc");

        assertEquals(1, result.getRank());
        assertEquals("TestTool", result.getToolName());
        assertEquals("TestProvider", result.getProvider());
        assertEquals("v1", result.getModelNumber());
        assertEquals(9.5, result.getScore(), 0.01);
        assertEquals("$10", result.getPricing());
        assertEquals("Desc", result.getDescription());
    }

    @Test
    void testProviderStat() {
        ProviderStat stat = new ProviderStat("AWS", 0.05, 9.2, 10);
        assertEquals("AWS", stat.getPlatform());
        assertEquals(10, stat.getCount());
        assertEquals(0.05, stat.getAvgCost(), 0.01);
        assertEquals(9.2, stat.getAvgPerformance(), 0.01);
        
        stat.setPlatform("GCP");
        stat.setCount(20);
        assertEquals("GCP", stat.getPlatform());
        assertEquals(20, stat.getCount());
    }

    @Test
    void testEnums() {
        ServiceType st = new ServiceType("COMPUTE", "Compute");
        assertEquals("COMPUTE", st.getValue());
        assertEquals("Compute", st.getLabel());
        
        Region r = new Region("us-east-1", "US East (N. Virginia)");
        assertEquals("us-east-1", r.getValue());
        assertEquals("US East (N. Virginia)", r.getLabel());
    }
}
