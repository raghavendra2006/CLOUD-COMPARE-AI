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

        // Reuse Groq tooling via existing method as an additive connectivity path.
        try {
            var results = grokClientService.fetchAiToolsComparisonFromGrok(question);
            if (results != null && !results.isEmpty() && results.get(0) != null) {
                var top = results.get(0);
                return ChatResponse.builder()
                        .reply("Architect reasoning (mock/approx): The top-ranked tool is recommended because it best fits your purpose and constraints."
                                + "\nTop hint: " + top.getToolName() + " by " + top.getProvider() + " (rating " + top.getScore() + ")")
                        .build();
            }
        } catch (Exception ignored) {
            // fall through
        }

        return ChatResponse.builder()
                .reply("Architect reasoning: I’m ready to explain the AI tools ranking. Run AI Tools comparison first (so context is available) and try again.")
                .build();
    }
}

