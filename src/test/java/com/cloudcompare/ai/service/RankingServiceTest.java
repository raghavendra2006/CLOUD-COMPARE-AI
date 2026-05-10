package com.cloudcompare.ai.service;

import com.cloudcompare.ai.dto.CompareResponse;
import com.cloudcompare.ai.dto.ServiceResult;
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
    void testToMethods() {
        // Use reflection or just public methods if accessible.
        // RankingService methods are private but we can test them via public buildResponse
        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> svc = new HashMap<>();
        svc.put("provider", "AWS");
        svc.put("cpu", "4 vCPUs");
        svc.put("ram", "16 GB");
        svc.put("price_per_hour", "$0.123");
        data.add(svc);

        CompareResponse response = rankingService.buildResponse(data, "compute", "all", 730, "all", 0, "balanced");
        ServiceResult aws = response.getServices().stream()
                .filter(s -> "AWS".equals(s.getPlatform()))
                .findFirst().orElse(null);
        assertNotNull(aws);
        assertEquals(4, aws.getCpu());
        assertEquals(16, aws.getRam());
        assertEquals(0.123, aws.getPricePerHour());
    }

    @Test
    void testPerformanceLevels() {
        List<Map<String, Object>> data = new ArrayList<>();
        
        Map<String, Object> svc1 = new HashMap<>();
        svc1.put("provider", "AWS");
        svc1.put("performance_score", 9.5);
        data.add(svc1);

        Map<String, Object> svc2 = new HashMap<>();
        svc2.put("provider", "GCP");
        svc2.put("performance_score", 8.0);
        data.add(svc2);

        Map<String, Object> svc3 = new HashMap<>();
        svc3.put("provider", "Azure");
        svc3.put("performance_score", 5.0);
        data.add(svc3);

        CompareResponse response = rankingService.buildResponse(data, "compute", "all", 730, "all", 0, "balanced");
        
        assertEquals("High", response.getServices().stream().filter(s -> "AWS".equals(s.getPlatform())).findFirst().get().getPerformanceLevel());
        assertEquals("Medium", response.getServices().stream().filter(s -> "GCP".equals(s.getPlatform())).findFirst().get().getPerformanceLevel());
        assertEquals("Low", response.getServices().stream().filter(s -> "Azure".equals(s.getPlatform())).findFirst().get().getPerformanceLevel());
    }

    @Test
    void testRecommendationReasons() {
        String[] categories = {"compute", "storage", "database", "ai", "unknown"};
        for (String cat : categories) {
            CompareResponse response = rankingService.buildResponse(new ArrayList<>(), cat, "all", 730, "all", 0, "balanced");
            assertNotNull(response.getRecommendation().getReason());
        }
    }

    @Test
    void testBuildResponseFromDb() {
        CompareResponse response = rankingService.buildResponseFromDb(
                new ArrayList<>(), "compute", "Virtual Machines", 730, "us-east-1", 0, "balanced"
        );
        assertNotNull(response);
        assertEquals(5, response.getServices().size());
    }

    @Test
    void testBuildResponseFromDbCostPriority() {
        CompareResponse response = rankingService.buildResponseFromDb(
                new ArrayList<>(), "storage", "Object Storage", 730, "us-east-1", 100, "cost"
        );
        assertNotNull(response);
        assertEquals(5, response.getServices().size());
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

    @Test
    void testParsingExceptionsAndNaN() {
        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> svc = new HashMap<>();
        svc.put("provider", "AWS");
        svc.put("cpu", "abc"); // triggers NumberFormatException
        svc.put("price_per_hour", "invalid"); // triggers NumberFormatException
        svc.put("performance_score", Double.NaN); // triggers NaN in round()
        data.add(svc);

        CompareResponse response = rankingService.buildResponse(data, "compute", "all", 730, "all", 0, "balanced");
        ServiceResult aws = response.getServices().stream().filter(s -> "AWS".equals(s.getPlatform())).findFirst().orElse(null);
        assertNotNull(aws);
        assertEquals(0, aws.getCpu());
        assertEquals(0.0, aws.getPricePerHour());
        assertTrue(Double.isNaN(aws.getPerformanceScore()));
    }

    @Test
    void testRecommendationReasonsFull() {
        String[] categories = {"database", "ai", "unknown"};
        for (String cat : categories) {
            CompareResponse response = rankingService.buildResponse(new ArrayList<>(), cat, "all", 730, "all", 0, "balanced");
            assertNotNull(response.getRecommendation().getReason());
        }
    }
}
