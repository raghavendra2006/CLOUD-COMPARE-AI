package com.cloudcompare.ai.controller;

import com.cloudcompare.ai.dto.AiCompareRequest;
import com.cloudcompare.ai.dto.AiToolResult;
import com.cloudcompare.ai.dto.ApiResponse;
import com.cloudcompare.ai.dto.CompareRequest;
import com.cloudcompare.ai.dto.CompareResponse;
import com.cloudcompare.ai.dto.Region;
import com.cloudcompare.ai.dto.ServiceType;
import com.cloudcompare.ai.dto.ChatRequest;
import com.cloudcompare.ai.dto.ChatResponse;
import com.cloudcompare.ai.dto.NlpQueryRequest;
import com.cloudcompare.ai.service.CacheService;
import com.cloudcompare.ai.service.GrokClientService;
import com.cloudcompare.ai.service.MetaDataService;
import com.cloudcompare.ai.service.RankingService;
import com.cloudcompare.ai.service.chat.AiToolsChatbotService;
import com.cloudcompare.ai.service.chat.CloudCompareChatbotService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * REST Controller — direct port of server.js + routes.js
 * Additive endpoints: cloud+AI chatbots + NLP override.
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    private static final Logger log = LoggerFactory.getLogger(ApiController.class);

    private final GrokClientService grokClientService;
    private final MetaDataService metaDataService;
    private final CacheService cacheService;
    private final RankingService rankingService;

    private final CloudCompareChatbotService cloudCompareChatbotService;
    private final AiToolsChatbotService aiToolsChatbotService;

    public ApiController(GrokClientService grokClientService,
                         MetaDataService metaDataService,
                         CacheService cacheService,
                         RankingService rankingService,
                         CloudCompareChatbotService cloudCompareChatbotService,
                         AiToolsChatbotService aiToolsChatbotService) {
        this.grokClientService = grokClientService;
        this.metaDataService = metaDataService;
        this.cacheService = cacheService;
        this.rankingService = rankingService;
        this.cloudCompareChatbotService = cloudCompareChatbotService;
        this.aiToolsChatbotService = aiToolsChatbotService;
    }

    @GetMapping("/test")
    public ApiResponse<Map<String, String>> healthCheck() {
        return ApiResponse.success(Map.of(
                "status", "ok",
                "message", "CloudCompare AI Engine is active."
        ));
    }

    @PostMapping("/compare")
    public ResponseEntity<Object> compare(@Valid @RequestBody CompareRequest req) {
        String category = req.getCategory();
        String svcType = (req.getServiceType() != null && !"all".equals(req.getServiceType()))
                ? req.getServiceType()
                : metaDataService.getDefaultServiceType(category);

        log.info("Processing comparison for {} -> {}", category, svcType);

        try {
            String cacheKey = "compare_" + category + "_" + svcType;
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> grokResults = (List<Map<String, Object>>) cacheService.get(cacheKey);

            if (grokResults == null) {
                log.info("Fetching fresh AI comparison for {}/{}", category, svcType);
                grokResults = grokClientService.fetchComparisonFromGrok(category, svcType);
                cacheService.set(cacheKey, grokResults);
            }

            CompareResponse response = rankingService.buildResponse(
                    grokResults, category, svcType,
                    req.getHours(), req.getRegion(),
                    req.getStorage(), req.getPriority()
            );

            return ResponseEntity.ok(ApiResponse.success(response));

        } catch (RuntimeException | IOException err) {
            log.error("Comparison failed: {}", err.getMessage());
            String errorMsg = err.getMessage();
            if (errorMsg != null && errorMsg.contains("YOUR_GROQ_API_KEYS_HERE")) {
                errorMsg = "AI Engine Configuration Missing. Please set the GROK_API_KEYS environment variable to enable live analysis.";
            }
            return ResponseEntity.status(502).body(ApiResponse.error(errorMsg));
        } catch (InterruptedException err) {
            log.error("Comparison interrupted: {}", err.getMessage());
            Thread.currentThread().interrupt();
            return ResponseEntity.status(502).body(ApiResponse.error("Analysis interrupted"));
        }
    }

    @GetMapping("/service-types/{category}")
    public ApiResponse<List<ServiceType>> getServiceTypes(@PathVariable String category) {
        return ApiResponse.success(metaDataService.getServiceTypes(category));
    }

    @GetMapping("/regions")
    public ApiResponse<List<Region>> getRegions() {
        return ApiResponse.success(metaDataService.getRegions());
    }

    @PostMapping("/ai-compare")
    public ResponseEntity<Object> compareAiTools(@Valid @RequestBody AiCompareRequest req) {
        String purpose = req.getPurpose();
        if (req.getQueryText() != null && !req.getQueryText().trim().isEmpty()) {
            purpose = req.getQueryText().trim();
        }
        log.info("AI Analysis request for: {}", purpose);

        try {
            String cacheKey = "ai_tool_" + purpose.toLowerCase().replaceAll("\\s+", "_");
            @SuppressWarnings("unchecked")
            List<AiToolResult> grokResults = (List<AiToolResult>) cacheService.get(cacheKey);

            if (grokResults == null) {
                grokResults = grokClientService.fetchAiToolsComparisonFromGrok(purpose);
                grokResults.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
                for (int i = 0; i < grokResults.size(); i++) {
                    grokResults.get(i).setRank(i + 1);
                }
                cacheService.set(cacheKey, grokResults);
            }

            return ResponseEntity.ok(ApiResponse.success(Map.of(
                    "purpose", purpose,
                    "totalResults", grokResults.size(),
                    "tools", grokResults
            )));

        } catch (RuntimeException | IOException err) {
            log.error("AI Tool Analysis failed: {}", err.getMessage());
            return ResponseEntity.status(502).body(ApiResponse.error("AI Analysis failed: " + err.getMessage()));
        } catch (InterruptedException err) {
            log.error("AI Tool Analysis interrupted: {}", err.getMessage());
            Thread.currentThread().interrupt();
            return ResponseEntity.status(502).body(ApiResponse.error("AI Analysis interrupted"));
        }
    }

    @PostMapping("/nlp-compare")
    public ResponseEntity<Object> compareAiToolsFromNaturalLanguage(@Valid @RequestBody NlpQueryRequest req) {
        String query = req.getQuery().trim();
        log.info("NLP AI Analysis request for: {}", query);

        try {
            String cacheKey = "nlp_tool_" + query.toLowerCase().replaceAll("\\s+", "_");
            @SuppressWarnings("unchecked")
            List<AiToolResult> grokResults = (List<AiToolResult>) cacheService.get(cacheKey);

            if (grokResults == null) {
                grokResults = grokClientService.fetchNlpComparisonFromGrok(query);
                grokResults.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
                for (int i = 0; i < grokResults.size(); i++) {
                    grokResults.get(i).setRank(i + 1);
                }
                cacheService.set(cacheKey, grokResults);
            }

            return ResponseEntity.ok(ApiResponse.success(Map.of(
                    "query", query,
                    "intent", grokClientService.classifyQueryIntent(query),
                    "totalResults", grokResults.size(),
                    "tools", grokResults
            )));

        } catch (RuntimeException | IOException err) {
            log.error("NLP AI Tool Analysis failed: {}", err.getMessage());
            return ResponseEntity.status(502).body(ApiResponse.error("NLP AI Analysis failed: " + err.getMessage()));
        } catch (InterruptedException err) {
            log.error("NLP AI Tool Analysis interrupted: {}", err.getMessage());
            Thread.currentThread().interrupt();
            return ResponseEntity.status(502).body(ApiResponse.error("NLP AI Analysis interrupted"));
        }
    }

    @PostMapping("/chat/cloud")
    public ResponseEntity<Object> chatCloud(@Valid @RequestBody ChatRequest req) {
        return buildChatResponse("cloud", () -> cloudCompareChatbotService.chat(req));
    }

    @PostMapping("/chat/ai-tools")
    public ResponseEntity<Object> chatAiTools(@Valid @RequestBody ChatRequest req) {
        return buildChatResponse("ai-tools", () -> aiToolsChatbotService.chat(req));
    }

    private ResponseEntity<Object> buildChatResponse(String mode, Supplier<ChatResponse> chatSupplier) {
        String fallbackReply = fallbackChatReply(mode);
        try {
            ChatResponse reply = chatSupplier.get();
            String replyText = reply != null ? reply.getReply() : null;
            if (replyText == null || replyText.isBlank()) {
                log.warn("Chatbot mode {} returned an empty reply; using fallback response", mode);
                replyText = fallbackReply;
            }
            return ResponseEntity.ok(ApiResponse.success(Map.of("reply", replyText)));
        } catch (Exception err) {
            log.error("Chatbot mode {} failed: {}", mode, err.getMessage(), err);
            return ResponseEntity.ok(ApiResponse.success(Map.of("reply", fallbackReply)));
        }
    }

    private String fallbackChatReply(String mode) {
        if ("ai-tools".equals(mode)) {
            return "AI Tools Architect guidance\n\n"
                    + "I could not reach the live AI tools reasoning engine right now, but you can still compare options safely: start with the top-ranked tool from your results, validate pricing and integrations with a small trial, then keep the next-ranked tool as a backup for cost, quality, or rate-limit gaps.";
        }
        return "Cloud Architect guidance\n\n"
                + "I could not reach the live cloud reasoning engine right now, but you can still move forward safely: use the top-ranked provider from your comparison as the first proof of concept, validate region availability and estimated monthly cost, then keep the second-ranked provider as a fallback for latency, compliance, or budget changes.";
    }
}
