package com.cloudcompare.ai.service.chat;

import com.cloudcompare.ai.dto.ChatRequest;
import com.cloudcompare.ai.dto.ChatResponse;
import com.cloudcompare.ai.service.GrokClientService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Additive chatbot for AI tools compare reasoning.
 */
@Service
public class AiToolsChatbotService {

    private final GrokClientService grokClientService;

    public AiToolsChatbotService(GrokClientService grokClientService) {
        this.grokClientService = grokClientService;
    }

    public ChatResponse chat(ChatRequest req) {
        String question = req.getQuestion();
        Map<String, Object> aiToolsContext = req.getAiToolsContext();

        try {
            var results = grokClientService.fetchAiToolsComparisonFromGrok(question);
            if (results != null && !results.isEmpty() && results.get(0) != null) {
                var top = results.get(0);
                return ChatResponse.builder()
                        .reply(buildAiToolsArchitectReply(
                                question,
                                top.getToolName(),
                                top.getProvider(),
                                top.getScore(),
                                aiToolsContext
                        ))
                        .build();
            }
        } catch (Exception ignored) {
            // fall through
        }

        return ChatResponse.builder()
                .reply("Architect reasoning: I’m ready to explain the AI tools ranking. Run AI Tools comparison first (so context is available) and try again.")
                .build();
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
