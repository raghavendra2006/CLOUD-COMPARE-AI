package com.cloudcompare.ai.service.chat;

import com.cloudcompare.ai.dto.ChatRequest;
import com.cloudcompare.ai.dto.ChatResponse;
import com.cloudcompare.ai.service.GrokClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Additive chatbot for AI tools compare reasoning.
 */
@Service
public class AiToolsChatbotService {

    private static final Logger log = LoggerFactory.getLogger(AiToolsChatbotService.class);
    private static final String DEFAULT_QUESTION = "Explain the top AI tool recommendation";

    private final GrokClientService grokClientService;

    public AiToolsChatbotService(GrokClientService grokClientService) {
        this.grokClientService = grokClientService;
    }

    public ChatResponse chat(ChatRequest req) {
        String question = normalizeQuestion(req);
        Map<String, Object> aiToolsContext = req != null ? req.getAiToolsContext() : null;

        try {
            var results = grokClientService.fetchAiToolsComparisonFromGrok(question);
            if (results != null && !results.isEmpty() && results.get(0) != null) {
                var top = results.get(0);
                String toolName = firstNonBlank(top.getToolName(), "the top-ranked AI tool");
                String provider = firstNonBlank(top.getProvider(), "the listed provider");
                return ChatResponse.builder()
                        .reply(buildAiToolsArchitectReply(
                                question,
                                toolName,
                                provider,
                                top.getScore(),
                                aiToolsContext
                        ))
                        .build();
            }
        } catch (Exception err) {
            log.warn("AI tools chatbot live reasoning failed; returning deterministic fallback: {}", err.getMessage());
        }

        return ChatResponse.builder()
                .reply("AI Tools Architect guidance\n\n"
                        + "Question: " + question + "\n\n"
                        + "I could not reach the live AI tools ranking engine, so here is a safe fallback plan: use your latest comparison results, trial the top-ranked tool on a small real task, validate pricing and integrations, and keep the next-ranked tool as a backup for quality, cost, or rate-limit gaps.")
                .build();
    }

    private String normalizeQuestion(ChatRequest req) {
        if (req == null || req.getQuestion() == null || req.getQuestion().isBlank()) {
            return DEFAULT_QUESTION;
        }
        return req.getQuestion().trim();
    }

    private String firstNonBlank(String value, String fallback) {
        return value != null && !value.isBlank() ? value : fallback;
    }

    private String buildAiToolsArchitectReply(String question,
                                              String toolName,
                                              String provider,
                                              double score,
                                              Map<String, Object> aiToolsContext) {
        int comparedTools = 0;
        if (aiToolsContext != null && aiToolsContext.get("tools") instanceof java.util.List<?> tools) {
            comparedTools = tools.size();
        }

        return "AI Tools Architect guidance\n\n"
                + "Question: " + question + "\n\n"
                + "Recommendation anchor: " + toolName + " by " + provider + " (score " + score + ")\n"
                + "Comparison context: " + (comparedTools > 0 ? comparedTools + " AI tool options were provided." : "Using purpose-based AI tool guidance.") + "\n\n"
                + "Selection plan:\n"
                + "1. Use the top-ranked tool for the primary workflow.\n"
                + "2. Validate pricing, API availability, integrations, and output quality with a small trial task.\n"
                + "3. Keep the next-ranked tool as a backup for cost, rate-limit, or feature gaps.\n"
                + "4. For team projects, compare ease of onboarding, documentation quality, and collaboration support before final adoption.";
    }
}
