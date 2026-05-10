package com.cloudcompare.ai.service;

import com.cloudcompare.ai.dto.CompareResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class RankingServiceTest {

    @Autowired
    private RankingService rankingService;

    @Test
    void testRankingLogic() {
        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> svc1 = new HashMap<>();
        svc1.put("provider", "AWS");
        svc1.put("name", "t3.medium");
        svc1.put("price_per_hour", 0.0416);
        svc1.put("performance_score", 8);
        data.add(svc1);

        CompareResponse response = rankingService.buildResponse(
                data, "compute", "all", 730, "all", 0, "balanced"
        );

        assertNotNull(response);
        com.cloudcompare.ai.dto.ServiceResult aws = response.getServices().stream()
                .filter(s -> "AWS".equals(s.getPlatform()))
                .findFirst().orElse(null);
        assertNotNull(aws);
        assertEquals("AWS", aws.getPlatform());
    }

    @Test
    void testRankingLogicWithCostPriority() {
        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> svc1 = new HashMap<>();
        svc1.put("provider", "AWS");
        svc1.put("price_per_hour", 0.1);
        svc1.put("performance_score", 9);
        data.add(svc1);

        Map<String, Object> svc2 = new HashMap<>();
        svc2.put("provider", "GCP");
        svc2.put("price_per_hour", 0.05);
        svc2.put("performance_score", 5);
        data.add(svc2);

        CompareResponse response = rankingService.buildResponse(
                data, "compute", "all", 730, "all", 0, "cost"
        );

        assertEquals("GCP", response.getServices().get(0).getPlatform());
    }

    @Test
    void testRankingLogicWithPerformancePriority() {
        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> svc1 = new HashMap<>();
        svc1.put("provider", "AWS");
        svc1.put("price_per_hour", 0.1);
        svc1.put("performance_score", 9);
        data.add(svc1);

        Map<String, Object> svc2 = new HashMap<>();
        svc2.put("provider", "GCP");
        svc2.put("price_per_hour", 0.05);
        svc2.put("performance_score", 5);
        data.add(svc2);

        CompareResponse response = rankingService.buildResponse(
                data, "compute", "all", 730, "all", 0, "performance"
        );

        assertEquals("AWS", response.getServices().get(0).getPlatform());
    }

    @Test
    void testRankingLogicForStorageCategory() {
        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> svc1 = new HashMap<>();
        svc1.put("provider", "AWS");
        svc1.put("price_per_gb", 0.02);
        data.add(svc1);

        CompareResponse response = rankingService.buildResponse(
                data, "storage", "all", 730, "all", 100, "balanced"
        );

        assertNotNull(response);
        com.cloudcompare.ai.dto.ServiceResult aws = response.getServices().stream()
                .filter(s -> "AWS".equals(s.getPlatform()))
                .findFirst().orElse(null);
        assertNotNull(aws);
        assertEquals(2.0, aws.getCost(), 0.01);
    }

    @Test
    void testFillMissingProviders() {
        List<Map<String, Object>> data = new ArrayList<>();
        // Empty data
        CompareResponse response = rankingService.buildResponse(
                data, "compute", "all", 730, "all", 0, "balanced"
        );

        assertEquals(5, response.getServices().size());
        assertTrue(response.getServices().stream().anyMatch(s -> "OCI".equals(s.getPlatform())));
    }
}
