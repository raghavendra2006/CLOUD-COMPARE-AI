package com.cloudcompare.ai.service.chat;

import com.cloudcompare.ai.dto.ChatRequest;
import com.cloudcompare.ai.dto.ChatResponse;
import com.cloudcompare.ai.service.GrokClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Additive chatbot for cloud compare reasoning.
 * Uses Groq via GrokClientService mock/real logic.
 */
@Service
public class CloudCompareChatbotService {

    private static final Logger log = LoggerFactory.getLogger(CloudCompareChatbotService.class);
    private static final String DEFAULT_QUESTION = "Explain the top cloud recommendation";

    private final GrokClientService grokClientService;

    public CloudCompareChatbotService(GrokClientService grokClientService) {
        this.grokClientService = grokClientService;
    }

    public ChatResponse chat(ChatRequest req) {
        String question = normalizeQuestion(req);
        Map<String, Object> cloudContext = req != null ? req.getCloudContext() : null;

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
            String topService = serviceType;
            if (cloudComparison != null && !cloudComparison.isEmpty()) {
                Map<String, Object> top = cloudComparison.get(0);
                Object prov = top.get("provider");
                if (prov instanceof String s && !s.isBlank()) topProvider = s;
                Object svc = top.get("service_name");
                if (svc instanceof String s && !s.isBlank()) topService = s;
            }

            return ChatResponse.builder()
                    .reply(buildCloudArchitectReply(question, category, serviceType, topProvider, topService, cloudContext))
                    .build();
        } catch (Exception err) {
            log.warn("Cloud chatbot live reasoning failed; returning deterministic fallback: {}", err.getMessage());
        }

        return ChatResponse.builder()
                .reply("Cloud Architect guidance\n\n"
                        + "Question: " + question + "\n\n"
                        + "I could not reach the live cloud comparison engine, so here is a safe fallback plan: run or reuse your latest comparison, start with the top-ranked provider for a proof of concept, validate region availability and estimated monthly cost, and keep the second-ranked provider as a fallback if latency, compliance, or budget requirements change.")
                .build();
    }

    private String normalizeQuestion(ChatRequest req) {
        if (req == null || req.getQuestion() == null || req.getQuestion().isBlank()) {
            return DEFAULT_QUESTION;
        }
        return req.getQuestion().trim();
    }

    private String buildCloudArchitectReply(String question,
                                            String category,
                                            String serviceType,
                                            String topProvider,
                                            String topService,
                                            Map<String, Object> cloudContext) {
        int comparedServices = 0;
        if (cloudContext != null && cloudContext.get("services") instanceof java.util.List<?> services) {
            comparedServices = services.size();
        }

        return "Cloud Architect guidance\n\n"
                + "Question: " + question + "\n\n"
                + "Recommendation anchor: " + topProvider + " - " + topService + "\n"
                + "Workload category: " + category + "\n"
                + "Service type: " + serviceType + "\n"
                + "Comparison context: " + (comparedServices > 0 ? comparedServices + " ranked service options were provided." : "Using generated provider guidance.") + "\n\n"
                + "Deployment plan:\n"
                + "1. Start with the top-ranked provider for the primary environment.\n"
                + "2. Validate region availability, network access, and estimated monthly cost before provisioning.\n"
                + "3. Use the second-ranked provider as a fallback option if cost, latency, or compliance constraints change.\n"
                + "4. For beginners, deploy a minimal proof of concept first, then scale CPU, memory, and storage after measuring usage.\n\n"
                + "Trade-off to review: choose cost priority for budget-sensitive workloads, performance priority for latency or throughput-sensitive workloads, and balanced priority for general production use.";
    }
}
