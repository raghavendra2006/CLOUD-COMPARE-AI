package com.cloudcompare.ai.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import com.cloudcompare.ai.dto.AiToolResult;
import com.cloudcompare.ai.exception.BusinessException;

/**
 * 🚀 OVER-EXCELLENCE: Groq API client with Resilience4J Circuit Breakers and Retries.
 * Executes on Java 21 Virtual Threads natively, scaling to thousands of concurrent requests.
 */
@Service
public class GrokClientService {

    private static final Logger log = LoggerFactory.getLogger(GrokClientService.class);

    @Value("${grok.api.keys}")
    private String apiKeysRaw;

    private List<String> getApiKeys() {
        if (apiKeysRaw == null || apiKeysRaw.isEmpty()) return Collections.emptyList();
        return Arrays.stream(apiKeysRaw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    @Value("${grok.endpoint}")
    private String endpoint;

    @Value("${grok.model}")
    private String model;

    @Value("${grok.timeout:15000}")
    private int timeoutMs;

    private final MockDataService mockDataService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final AtomicInteger keyIndex = new AtomicInteger(0);

    public GrokClientService(MockDataService mockDataService) {
        this.mockDataService = mockDataService;
    }

    private String getNextApiKey() {
        List<String> keys = getApiKeys();
        if (keys.isEmpty()) return "YOUR_GROQ_API_KEYS_HERE";
        return keys.get(Math.abs(keyIndex.getAndIncrement() % keys.size()));
    }

    /**
     * Protected by Resilience4J Retry and CircuitBreaker.
     * If the API fails repeatedly, it trips the circuit and calls the fallback method gracefully.
     */
    @Retry(name = "groqApi", fallbackMethod = "fetchComparisonFallback")
    @CircuitBreaker(name = "groqApi", fallbackMethod = "fetchComparisonFallback")
    public List<Map<String, Object>> fetchComparisonFromGrok(String category, String serviceType) throws IOException, InterruptedException {
        String apiKey = getNextApiKey();
        if ("YOUR_GROQ_API_KEYS_HERE".equals(apiKey) || apiKey.isEmpty()) {
            log.info("Using mock Groq response because API key is placeholder.");
            return mockDataService.getMockComparison(serviceType);
        }

        String prompt = buildPrompt(category, serviceType);
        String rawResponse = callGroqApi(prompt, apiKey);
        String cleaned = extractJson(rawResponse);
        return objectMapper.readValue(cleaned, new TypeReference<>() {});
    }

    /**
     * Fallback method when Circuit Breaker is OPEN or Retries are exhausted.
     */
    @SuppressWarnings("unused")
    public List<Map<String, Object>> fetchComparisonFallback(String category, String serviceType, Throwable t) {
        log.warn("Groq API Circuit Breaker tripped / Retries exhausted. Falling back to static cache. Reason: {}", t.getMessage(), t);
        return mockDataService.getMockComparison(serviceType);
    }

    @Retry(name = "groqApi", fallbackMethod = "fetchAiToolsFallback")
    @CircuitBreaker(name = "groqApi", fallbackMethod = "fetchAiToolsFallback")
    public List<AiToolResult> fetchAiToolsComparisonFromGrok(String purpose) throws IOException, InterruptedException {
        String apiKey = getNextApiKey();
        if ("YOUR_GROQ_API_KEYS_HERE".equals(apiKey) || apiKey.isEmpty()) {
            log.info("Using purpose-aware mock data for AI tools. Purpose: {}", purpose);
            return mockDataService.getMockAiToolsForPurpose(purpose);
        }

        String prompt = buildAiPrompt(purpose);
        String rawResponse = callGroqApi(prompt, apiKey);
        String cleaned = extractJson(rawResponse);
        return objectMapper.readValue(cleaned, new TypeReference<>() {});
    }

    /**
     * Fallback method when Circuit Breaker is OPEN or Retries are exhausted.
     */
    @SuppressWarnings("unused")
    public List<AiToolResult> fetchAiToolsFallback(String purpose, Throwable t) {
        log.warn("Groq API Circuit Breaker tripped for AI Tools. Falling back to purpose-aware mock data. Reason: {}", t.getMessage(), t);
        return mockDataService.getMockAiToolsForPurpose(purpose);
    }

    private String callGroqApi(String prompt, String apiKey) throws IOException, InterruptedException {
        String requestBody = objectMapper.writeValueAsString(Map.of(
                "model", model,
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "temperature", 0.1,
                "max_tokens", 2000
        ));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .timeout(Duration.ofMillis(timeoutMs))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new BusinessException("Groq API " + response.statusCode() + ": " + response.body());
        }

        Map<String, Object> data = objectMapper.readValue(response.body(), new TypeReference<>() {});
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> choices = (List<Map<String, Object>>) data.get("choices");
        @SuppressWarnings("unchecked")
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        return ((String) message.get("content")).trim();
    }

    private String extractJson(String raw) {
        if (raw == null || raw.isEmpty()) return "[]";
        
        int startBracket = raw.indexOf('[');
        int startBrace = raw.indexOf('{');
        int start = -1;
        
        if (startBracket != -1 && (startBrace == -1 || startBracket < startBrace)) {
            start = startBracket;
        } else if (startBrace != -1) {
            start = startBrace;
        }
        
        if (start == -1) return raw;

        int endBracket = raw.lastIndexOf(']');
        int endBrace = raw.lastIndexOf('}');
        int end = Math.max(endBracket, endBrace);
        
        if (end == -1 || end <= start) return raw;
        
        return raw.substring(start, end + 1);
    }

    private String buildPrompt(String category, String serviceType) {
        return "You are an expert cloud infrastructure analyst. Compare the \"" + serviceType +
                "\" services offered by AWS, Google Cloud (GCP), Microsoft Azure, Oracle Cloud (OCI), and Alibaba Cloud under the \"" +
                category + "\" category.\n\n" +
                "Return a JSON array with EXACTLY 5 objects (one per provider). Each object must have:\n" +
                "{\n" +
                "  \"provider\": \"<AWS|GCP|Azure|OCI|Alibaba>\",\n" +
                "  \"service_name\": \"<official product name>\",\n" +
                "  \"performance_score\": <1-10 numerical value>,\n" +
                "  \"popularity_score\": <1-10 numerical value>,\n" +
                "  \"price_per_hour\": <RAW NUMBER ONLY, e.g. 0.045, no symbols>,\n" +
                "  \"price_per_gb\": <RAW NUMBER ONLY, e.g. 0.02, no symbols>,\n" +
                "  \"cpu\": <RAW NUMBER ONLY, number of vCPUs>,\n" +
                "  \"ram\": <RAW NUMBER ONLY, GB of RAM>,\n" +
                "  \"storage\": <RAW NUMBER ONLY, GB of storage>,\n" +
                "  \"region\": \"<code-e.g.-us-east-1>\",\n" +
                "  \"description\": \"<text>\"\n" +
                "}\n\n" +
                "IMPORTANT: Return ONLY raw numbers for cost, cpu, ram, and scores. Do NOT include currency symbols or units in the values.\n" +
                "Output ONLY the raw JSON array.";
    }

    private String buildAiPrompt(String purpose) {
        return "Recommend top 5 AI tools for: \"" + purpose + "\".\n\n" +
                "Return a JSON array with EXACTLY 5 objects:\n" +
                "{\n" +
                "  \"tool_name\": \"<name>\",\n" +
                "  \"provider\": \"<company>\",\n" +
                "  \"model_number\": \"<model>\",\n" +
                "  \"score\": <1-10>,\n" +
                "  \"pricing\": \"<text>\",\n" +
                "  \"description\": \"<text>\"\n" +
                "}\n\n" +
                "Output ONLY raw JSON.";
    }
}
