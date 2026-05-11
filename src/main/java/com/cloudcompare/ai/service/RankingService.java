package com.cloudcompare.ai.service;

import com.cloudcompare.ai.dto.CompareResponse;
import com.cloudcompare.ai.dto.ProviderStat;
import com.cloudcompare.ai.dto.ServiceResult;
import com.cloudcompare.ai.entity.CloudServiceEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Ranking and response builder — exact port of rankAndRespond() from routes.js
 */
@Service
public class RankingService {

    private static final String STORAGE_KEY = "storage";
    private static final String REGION_KEY = "region";

    private static final double DEFAULT_PERF_WEIGHT = 0.40;
    private static final double DEFAULT_POP_WEIGHT = 0.25;
    private static final double DEFAULT_COST_WEIGHT = 0.35;

    private static final double COST_PRIORITY_PERF = 0.20;
    private static final double COST_PRIORITY_POP = 0.15;
    private static final double COST_PRIORITY_COST = 0.65;

    private static final double PERF_PRIORITY_PERF = 0.55;
    private static final double PERF_PRIORITY_POP = 0.20;
    private static final double PERF_PRIORITY_COST = 0.25;

    private static final int HOURS_IN_MONTH = 730;
    private static final int HOURS_IN_DAY = 24;
    private static final int DAYS_IN_WEEK = 7;

    private static final List<String> ALL_PROVIDERS = List.of("AWS", "GCP", "Azure", "OCI", "Alibaba");

    private static final Map<String, String> REGION_DEFAULTS = Map.of(
            "AWS", "us-east-1",
            "GCP", "us-central1",
            "Azure", "eastus",
            "OCI", "us-ashburn-1",
            "Alibaba", "ap-southeast-1"
    );

    /**
     * Normalise Groq output into the UI schema, guarantee all 5 providers,
     * then rank and build the full CompareResponse.
     */
    public CompareResponse buildResponse(
            List<Map<String, Object>> grokResults,
            String category, String serviceType,
            int hours, String region, int storage, String priority) {

        // Normalise Groq output into the UI schema
        Map<String, ServiceResult> merged = new LinkedHashMap<>();

        for (Map<String, Object> g : grokResults) {
            String provider = (String) g.get("provider");
            ServiceResult sr = new ServiceResult();
            sr.setId(0);
            sr.setPlatform(provider);
            sr.setServiceName(g.get("service_name") != null ? (String) g.get("service_name") : provider + " " + serviceType);
            sr.setCategory(category);
            sr.setCpu(toInt(g.get("cpu")));
            sr.setRam(toInt(g.get("ram")));
            sr.setStorage(toInt(g.get(STORAGE_KEY)));
            sr.setPricePerHour(toDouble(g.get("price_per_hour")));
            sr.setPricePerGb(toDouble(g.get("price_per_gb")));
            sr.setPerformanceScore(g.get("performance_score") != null ? toDouble(g.get("performance_score")) : 7);
            sr.setPopularityScore(g.get("popularity_score") != null ? toDouble(g.get("popularity_score")) : 7);
            sr.setRegion(g.get(REGION_KEY) != null ? (String) g.get(REGION_KEY) : REGION_DEFAULTS.getOrDefault(provider, "global"));
            sr.setDescription(g.get("description") != null ? (String) g.get("description") : serviceType);
            merged.put(provider, sr);
        }

        // Guarantee all 5 providers exist
        fillMissingProviders(merged, category, serviceType);

        List<ServiceResult> results = new ArrayList<>(merged.values());
        return rankAndRespond(results, category, hours, region, storage, priority);
    }

    /**
     * Build response from Database entities
     */
    public CompareResponse buildResponseFromDb(
            List<CloudServiceEntity> dbResults,
            String category, String serviceType,
            int hours, String region, int storage, String priority) {

        Map<String, ServiceResult> merged = new LinkedHashMap<>();

        for (CloudServiceEntity entity : dbResults) {
            ServiceResult sr = new ServiceResult();
            sr.setId(entity.getId().intValue());
            sr.setPlatform(entity.getPlatform());
            sr.setServiceName(entity.getServiceName());
            sr.setCategory(entity.getCategory());
            sr.setCpu(entity.getCpu());
            sr.setRam(entity.getRam());
            sr.setStorage(entity.getStorage());
            sr.setPricePerHour(entity.getPricePerHour());
            sr.setPricePerGb(entity.getPricePerGb());
            sr.setPerformanceScore(entity.getPerformanceScore());
            sr.setPopularityScore(entity.getPopularityScore());
            sr.setRegion(entity.getRegion());
            sr.setDescription(entity.getDescription());
            merged.put(entity.getPlatform(), sr);
        }

        // Guarantee all 5 providers exist
        fillMissingProviders(merged, category, serviceType);

        List<ServiceResult> results = new ArrayList<>(merged.values());
        return rankAndRespond(results, category, hours, region, storage, priority);
    }

    private void fillMissingProviders(Map<String, ServiceResult> merged, String category, String serviceType) {
        for (String prov : ALL_PROVIDERS) {
            if (!merged.containsKey(prov)) {
                ServiceResult sr = new ServiceResult();
                sr.setId(0);
                sr.setPlatform(prov);
                sr.setServiceName(prov + " " + serviceType);
                sr.setCategory(category);
                sr.setCpu(0);
                sr.setRam(0);
                sr.setStorage(0);
                sr.setPricePerHour(0);
                sr.setPricePerGb(0);
                sr.setPerformanceScore(5);
                sr.setPopularityScore(5);
                sr.setRegion(REGION_DEFAULTS.getOrDefault(prov, "global"));
                sr.setDescription(serviceType + " (data pending)");
                merged.put(prov, sr);
            }
        }
    }

    private CompareResponse rankAndRespond(
            List<ServiceResult> results, String category,
            int hours, String region, int storage, String priority) {

        Weights w = determineWeights(priority);
        double performanceWeight = w.performance;
        double popularityWeight = w.popularity;
        double costWeight = w.cost;

        // Precompute cost bounds
        double[] costValues = new double[results.size()];
        for (int i = 0; i < results.size(); i++) {
            ServiceResult r = results.get(i);
            if (STORAGE_KEY.equals(category)) {
                costValues[i] = r.getPricePerGb() * (storage > 0 ? storage : 1000);
            } else {
                costValues[i] = r.getPricePerHour() * (hours > 0 ? hours : 1);
            }
        }
        double costMin = Arrays.stream(costValues).min().orElse(0);
        double costMax = Arrays.stream(costValues).max().orElse(0);

        ProcessingContext ctx = new ProcessingContext(category, hours, storage, performanceWeight, popularityWeight, costWeight, costMin, costMax);
        List<ServiceResult> processed = processResults(results, ctx);

        // Sort by score descending
        processed.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

        // Assign ranks
        for (int i = 0; i < processed.size(); i++) {
            processed.get(i).setRank(i + 1);
        }

        // Provider stats aggregation
        Map<String, double[]> provMap = new LinkedHashMap<>();
        // provMap values: [totalCost, totalPerf, count]
        for (ServiceResult s : processed) {
            provMap.computeIfAbsent(s.getPlatform(), k -> new double[3]);
            double[] vals = provMap.get(s.getPlatform());
            vals[0] += s.getCost();
            vals[1] += s.getPerformanceScore();
            vals[2] += 1;
        }

        List<ProviderStat> providerStats = provMap.entrySet().stream()
                .map(e -> new ProviderStat(
                        e.getKey(),
                        round(e.getValue()[0] / e.getValue()[2], 2),
                        round(e.getValue()[1] / e.getValue()[2], 1),
                        (int) e.getValue()[2]
                ))
                .toList();

        // Recommendation = top service with reason
        ServiceResult recommendation = null;
        if (!processed.isEmpty()) {
            recommendation = cloneResult(processed.get(0));
            recommendation.setReason(getRecommendationReason(recommendation, category));
        }

        // Build response
        CompareResponse resp = new CompareResponse();
        resp.setCategory(category);
        resp.setFilters(Map.of(
                "hours", hours,
                REGION_KEY, region != null ? region : "all",
                "cpu", 0,
                "ram", 0,
                STORAGE_KEY, storage > 0 ? storage : 0
        ));
        resp.setTotalResults(processed.size());
        resp.setServices(processed);
        resp.setProviderStats(providerStats);
        resp.setRecommendation(recommendation);

        return resp;
    }

    // ============================================================
    // HELPERS
    // ============================================================

    private List<ServiceResult> processResults(List<ServiceResult> results, ProcessingContext ctx) {
        List<ServiceResult> processed = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            ServiceResult s = results.get(i);
            CostDetails cd = calculateCosts(s, ctx.category(), ctx.hours(), ctx.storage());
            double costVal = cd.costVal;
            double costPerHour = cd.costPerHour;

            double costPerDay = costPerHour * HOURS_IN_DAY;
            double costPerWeek = costPerDay * DAYS_IN_WEEK;
            double costPerMonth = costPerHour * HOURS_IN_MONTH;

            double performanceNorm = s.getPerformanceScore() > 0 ? s.getPerformanceScore() : 5;
            double popularityNorm = s.getPopularityScore() > 0 ? s.getPopularityScore() : 5;

            double costScore = 5;
            if (ctx.costMax() > ctx.costMin() && costVal > 0) {
                costScore = 10 * (1 - (costVal - ctx.costMin()) / (ctx.costMax() - ctx.costMin()));
            } else if (costVal == ctx.costMin() && costVal > 0) {
                costScore = 10;
            }

            double score = (performanceNorm * ctx.pw()) + (popularityNorm * ctx.ppw()) + (costScore * ctx.cw());

            ServiceResult p = new ServiceResult();
            p.setId(s.getId());
            p.setPlatform(s.getPlatform());
            p.setServiceName(s.getServiceName());
            p.setCategory(s.getCategory());
            p.setCpu(s.getCpu());
            p.setRam(s.getRam());
            p.setStorage(s.getStorage());
            p.setPricePerHour(s.getPricePerHour());
            p.setPricePerGb(s.getPricePerGb());
            p.setPerformanceScore(s.getPerformanceScore());
            p.setPopularityScore(s.getPopularityScore());
            p.setRegion(s.getRegion());
            p.setDescription(s.getDescription());
            p.setCost(round(costVal, 2));
            p.setCostPerHour(round(costPerHour, 4));
            p.setCostPerDay(round(costPerDay, 2));
            p.setCostPerWeek(round(costPerWeek, 2));
            p.setCostPerMonth(round(costPerMonth, 2));
            p.setScore(round(score, 2));
            p.setPerformanceLevel(getPerformanceLevel(s.getPerformanceScore()));
            p.setCostScore(round(costScore, 2));

            processed.add(p);
        }
        return processed;
    }

    private String getPerformanceLevel(double score) {
        if (score >= 9) return "High";
        if (score >= 7.5) return "Medium";
        return "Low";
    }

    private String getRecommendationReason(ServiceResult service, String category) {
        Map<String, String> reasons = Map.of(
                "compute", "Best performance-to-cost ratio with " + service.getPerformanceLevel().toLowerCase() + " performance and excellent scalability",
                STORAGE_KEY, "Optimal storage solution with " + service.getPerformanceLevel().toLowerCase() + " durability and competitive pricing",
                "database", "Recommended for production workloads with excellent reliability and managed features",
                "ai", "Top-rated AI service with " + service.getPerformanceLevel().toLowerCase() + " accuracy and strong ecosystem"
        );
        return reasons.getOrDefault(category, "Best overall value for your requirements");
    }

    private ServiceResult cloneResult(ServiceResult s) {
        ServiceResult c = new ServiceResult();
        BeanUtils.copyProperties(s, c);
        return c;
    }

    private Weights determineWeights(String priority) {
        if ("cost".equals(priority)) {
            return new Weights(COST_PRIORITY_PERF, COST_PRIORITY_POP, COST_PRIORITY_COST);
        } else if ("performance".equals(priority)) {
            return new Weights(PERF_PRIORITY_PERF, PERF_PRIORITY_POP, PERF_PRIORITY_COST);
        }
        return new Weights(DEFAULT_PERF_WEIGHT, DEFAULT_POP_WEIGHT, DEFAULT_COST_WEIGHT);
    }

    private CostDetails calculateCosts(ServiceResult s, String category, int hours, int storage) {
        double costVal;
        double costPerHour;
        if (STORAGE_KEY.equals(category)) {
            double msc = s.getPricePerGb() * (storage > 0 ? storage : 1000);
            costPerHour = msc / HOURS_IN_MONTH;
            costVal = costPerHour * (hours > 0 ? hours : HOURS_IN_MONTH);
        } else {
            costPerHour = s.getPricePerHour();
            costVal = costPerHour * (hours > 0 ? hours : HOURS_IN_MONTH);
        }
        return new CostDetails(costVal, costPerHour);
    }

    private record ProcessingContext(String category, int hours, int storage, double pw, double ppw, double cw, double costMin, double costMax) {}

    private static class Weights {
        double performance;
        double popularity;
        double cost;
        Weights(double perf, double pop, double cost) {
            this.performance = perf;
            this.popularity = pop;
            this.cost = cost;
        }
    }

    private static class CostDetails {
        double costVal;
        double costPerHour;
        CostDetails(double costVal, double costPerHour) {
            this.costVal = costVal;
            this.costPerHour = costPerHour;
        }
    }

    private double round(double value, int places) {
        if (Double.isNaN(value) || Double.isInfinite(value)) return 0;
        return BigDecimal.valueOf(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
    }

    private int toInt(Object val) {
        if (val == null) return 0;
        if (val instanceof Number number) return number.intValue();
        try {
            // Strip any non-digit characters except for initial parsing
            String cleaned = val.toString().replaceAll("[^0-9.-]", "");
            return cleaned.isEmpty() ? 0 : (int) Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            // SonarQube: ignore exception because we fallback to 0
            return 0;
        }
    }

    private double toDouble(Object val) {
        if (val == null) return 0;
        if (val instanceof Number number) return number.doubleValue();
        try {
            // Strip currency symbols, commas, etc.
            String cleaned = val.toString().replaceAll("[^0-9.-]", "");
            return cleaned.isEmpty() ? 0 : Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            // SonarQube: ignore exception because we fallback to 0
            return 0;
        }
    }
}
