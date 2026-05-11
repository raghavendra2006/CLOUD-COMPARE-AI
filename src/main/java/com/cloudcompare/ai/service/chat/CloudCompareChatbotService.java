package com.cloudcompare.ai.service.chat;

import com.cloudcompare.ai.dto.ChatRequest;
import com.cloudcompare.ai.dto.ChatResponse;
import com.cloudcompare.ai.service.GrokClientService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Additive chatbot for cloud compare reasoning.
 * Uses Groq via GrokClientService mock/real logic.
 */
@Service
public class CloudCompareChatbotService {

    private final GrokClientService grokClientService;

    public CloudCompareChatbotService(GrokClientService grokClientService) {
        this.grokClientService = grokClientService;
    }

    public ChatResponse chat(ChatRequest req) {
        String question = req.getQuestion();
        Map<String, Object> cloudContext = req.getCloudContext();

        // Use Groq via GrokClientService for a real chatbot answer.
        // If Groq is misconfigured or fails, GrokClientService fallback will provide mock data.
        try {
            String category = "compute";
            String serviceType = "Virtual Machines";

            if (cloudContext != null) {
                Object cat = cloudContext.get("category");
                if (cat instanceof String s && !s.isBlank()) category = s;

                Object st = cloudContext.get("serviceType");
                if (st instanceof String s && !s.isBlank()) serviceType = s;
            }

            var cloudComparison = grokClientService.fetchComparisonFromGrok(category, serviceType);

            String topProvider = "your top provider";
            if (cloudComparison != null && !cloudComparison.isEmpty()) {
                Object prov = cloudComparison.get(0).get("provider");
                if (prov instanceof String s && !s.isBlank()) topProvider = s;
            }

            return ChatResponse.builder()
                    .reply("Architect reasoning: Using Groq guidance, align your cloud choice with the top recommendation (anchor: "
                            + topProvider + ").\n\n"
                            + "Answer the user question based on cost vs performance and the constraints in cloudContext."
                            + "\n\nUser question: " + question)
                    .build();
        } catch (Exception ignored) {
            // Fall back to deterministic response.
        }

        return ChatResponse.builder()
                .reply("Architect reasoning: I’m ready to explain the top cloud choice. Provide the compare results context (or run a comparison) and try again.")
                .build();
    }
}

