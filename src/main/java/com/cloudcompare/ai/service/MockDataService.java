package com.cloudcompare.ai.service;

import com.cloudcompare.ai.dto.AiToolResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service to provide fallback mock data when API keys are missing.
 * Keeps core logic clean and separate from demo data.
 */
@Service
public class MockDataService {

    private static final String PRICE_PER_GB = "price_per_gb";
    private static final String POPULARITY_SCORE = "popularity_score";
    private static final String SERVICE_NAME = "service_name";
    private static final String PRICE_PER_HOUR = "price_per_hour";
    private static final String PERFORMANCE_SCORE = "performance_score";
    private static final String STORAGE = "storage";
    private static final String REGION = "region";
    private static final String DESCRIPTION = "description";
    private static final String PROVIDER = "provider";

    private static final String FREE_TIER_20_PRO = "Free tier, $20/mo Pro";
    private static final String ANTHROPIC = "Anthropic";
    private static final String CLAUDE_SONNET = "Claude 3.5 Sonnet";
    private static final String FREE_TIER_20_PLUS = "Free tier, $20/mo Plus";
    private static final String OPENAI = "OpenAI";
    private static final String GPT_4O = "GPT-4o";
    private static final String CLAUDE = "Claude";
    private static final String CHATGPT = "ChatGPT";
    private static final String GEMINI_PRO = "Gemini 1.5 Pro";
    private static final String GOOGLE = "Google";

    public List<Map<String, Object>> getMockComparison(String serviceType) {
        return new ArrayList<>(List.of(
            Map.ofEntries(Map.entry(PROVIDER, "AWS"), Map.entry(SERVICE_NAME, "AWS " + serviceType), Map.entry(PERFORMANCE_SCORE, 9.2), Map.entry(POPULARITY_SCORE, 9.8), Map.entry(PRICE_PER_HOUR, 0.05), Map.entry(PRICE_PER_GB, 0.0), Map.entry("cpu", 2), Map.entry("ram", 4), Map.entry(STORAGE, 100), Map.entry(REGION, "us-east-1"), Map.entry(DESCRIPTION, "Highly reliable and scalable.")),
            Map.ofEntries(Map.entry(PROVIDER, "GCP"), Map.entry(SERVICE_NAME, "Google " + serviceType), Map.entry(PERFORMANCE_SCORE, 9.5), Map.entry(POPULARITY_SCORE, 9.0), Map.entry(PRICE_PER_HOUR, 0.045), Map.entry(PRICE_PER_GB, 0.0), Map.entry("cpu", 2), Map.entry("ram", 4), Map.entry(STORAGE, 100), Map.entry(REGION, "us-central1"), Map.entry(DESCRIPTION, "Excellent performance and analytics integration.")),
            Map.ofEntries(Map.entry(PROVIDER, "Azure"), Map.entry(SERVICE_NAME, "Azure " + serviceType), Map.entry(PERFORMANCE_SCORE, 9.0), Map.entry(POPULARITY_SCORE, 9.5), Map.entry(PRICE_PER_HOUR, 0.048), Map.entry(PRICE_PER_GB, 0.0), Map.entry("cpu", 2), Map.entry("ram", 4), Map.entry(STORAGE, 100), Map.entry(REGION, "eastus"), Map.entry(DESCRIPTION, "Seamless enterprise integration.")),
            Map.ofEntries(Map.entry(PROVIDER, "OCI"), Map.entry(SERVICE_NAME, "Oracle " + serviceType), Map.entry(PERFORMANCE_SCORE, 8.8), Map.entry(POPULARITY_SCORE, 7.5), Map.entry(PRICE_PER_HOUR, 0.035), Map.entry(PRICE_PER_GB, 0.0), Map.entry("cpu", 2), Map.entry("ram", 4), Map.entry(STORAGE, 100), Map.entry(REGION, "us-ashburn-1"), Map.entry(DESCRIPTION, "Cost-effective for high workloads.")),
            Map.ofEntries(Map.entry(PROVIDER, "Alibaba"), Map.entry(SERVICE_NAME, "Alibaba " + serviceType), Map.entry(PERFORMANCE_SCORE, 8.5), Map.entry(POPULARITY_SCORE, 8.0), Map.entry(PRICE_PER_HOUR, 0.038), Map.entry(PRICE_PER_GB, 0.0), Map.entry("cpu", 2), Map.entry("ram", 4), Map.entry(STORAGE, 100), Map.entry(REGION, "ap-southeast-1"), Map.entry(DESCRIPTION, "Strong presence in APAC with competitive pricing."))
        ));
    }

    /**
     * Returns purpose-aware AI tool recommendations.
     * Each category returns the best 5 tools specifically for that purpose.
     */
    public List<AiToolResult> getMockAiToolsForPurpose(String purpose) {
        if (purpose == null || purpose.isEmpty()) {
            return getMockAiTools();
        }

        String lower = purpose.toLowerCase();

        return switch (lower) {
            case String s when s.contains("coding") || s.contains("software") || s.contains("development") -> getCodingTools();
            case String s when s.contains("content") || s.contains("writing") || s.contains("copywriting") -> getWritingTools();
            case String s when s.contains("data") || s.contains("analysis") || s.contains("spreadsheet") -> getDataTools();
            case String s when s.contains("image") || s.contains("design") || s.contains("graphic") -> getImageTools();
            case String s when s.contains("video") || s.contains("editing") -> getVideoTools();
            case String s when s.contains("presentation") || s.contains("slide") -> getPresentationTools();
            case String s when s.contains("music") || s.contains("audio") -> getMusicTools();
            case String s when s.contains("research") || s.contains("chat") || s.contains("general") -> getResearchTools();
            default -> getMockAiTools();
        };
    }

    public List<AiToolResult> getMockAiTools() {
        return new ArrayList<>(List.of(
            createAiTool(1, CHATGPT, OPENAI, GPT_4O, 9.8, FREE_TIER_20_PRO, "Excellent general purpose AI."),
            createAiTool(2, CLAUDE, ANTHROPIC, CLAUDE_SONNET, 9.6, FREE_TIER_20_PRO, "Superior reasoning and writing."),
            createAiTool(3, "Gemini", GOOGLE, GEMINI_PRO, 9.3, "Free tier, $20/mo Advanced", "Deep integration with Google Workspace."),
            createAiTool(4, "Copilot", "Microsoft", "GPT-4", 9.0, "Included in M365, $30/mo", "Best for enterprise productivity."),
            createAiTool(5, "Perplexity", "Perplexity AI", "Sonar", 8.8, FREE_TIER_20_PRO, "Outstanding for research and search.")
        ));
    }

    private List<AiToolResult> getCodingTools() {
        return new ArrayList<>(List.of(
            createAiTool(1, "GitHub Copilot", "GitHub / Microsoft", "Codex + GPT-4", 9.7, "$10/mo Individual, $19/mo Business", "AI pair programmer that autocompletes code in real-time across all major IDEs."),
            createAiTool(2, "Cursor", "Anysphere", "GPT-4 / Claude 3.5", 9.5, FREE_TIER_20_PRO, "AI-native code editor built from VS Code."),
            createAiTool(3, CLAUDE + " (Coding)", ANTHROPIC, CLAUDE_SONNET, 9.4, FREE_TIER_20_PRO, "Exceptional at complex reasoning."),
            createAiTool(4, CHATGPT + " (Code Interpreter)", OPENAI, GPT_4O, 9.2, FREE_TIER_20_PLUS, "Versatile coding assistant."),
            createAiTool(5, "Tabnine", "Tabnine", "Custom LLM", 8.6, "Free tier, $12/mo Pro", "Privacy-focused AI code completion.")
        ));
    }

    private List<AiToolResult> getWritingTools() {
        return new ArrayList<>(List.of(
            createAiTool(1, CLAUDE, ANTHROPIC, CLAUDE_SONNET, 9.8, FREE_TIER_20_PRO, "Best-in-class for long-form writing."),
            createAiTool(2, CHATGPT, OPENAI, GPT_4O, 9.5, FREE_TIER_20_PLUS, "Versatile content creation."),
            createAiTool(3, "Jasper", "Jasper AI", "Multi-model", 9.2, "$49/mo Creator, $125/mo Pro", "Purpose-built for marketing content."),
            createAiTool(4, "Copy.ai", "Copy.ai", "GPT-4 based", 8.9, "Free tier, $49/mo Pro", "Specialized in short-form marketing copy."),
            createAiTool(5, "Writesonic", "Writesonic", "GPT-4 based", 8.5, FREE_TIER_20_PRO, "AI writer with built-in SEO optimization.")
        ));
    }

    private List<AiToolResult> getDataTools() {
        return new ArrayList<>(List.of(
            createAiTool(1, CHATGPT + " (Code Interpreter)", OPENAI, GPT_4O, 9.6, FREE_TIER_20_PLUS, "Upload datasets, run Python analysis."),
            createAiTool(2, "Julius AI", "Julius AI", "Multi-model", 9.3, FREE_TIER_20_PRO, "Dedicated data analysis AI."),
            createAiTool(3, CLAUDE, ANTHROPIC, CLAUDE_SONNET, 9.1, FREE_TIER_20_PRO, "Excellent at interpreting complex datasets."),
            createAiTool(4, "Google Sheets AI (Gemini)", GOOGLE, GEMINI_PRO, 8.8, "Included in Workspace, $20/mo AI Premium", "Built-in AI for Google Sheets."),
            createAiTool(5, "Tableau GPT", "Salesforce", "Einstein GPT", 8.5, "$75/mo Creator", "Enterprise-grade data visualization.")
        ));
    }

    private List<AiToolResult> getImageTools() {
        return new ArrayList<>(List.of(
            createAiTool(1, "Midjourney", "Midjourney Inc.", "Midjourney V6.1", 9.8, "$10/mo Basic, $30/mo Standard", "Industry-leading image generation."),
            createAiTool(2, "DALL-E 3", OPENAI, "DALL-E 3", 9.4, "Included in ChatGPT Plus $20/mo", "Highly accurate text-to-image generation."),
            createAiTool(3, "Adobe Firefly", "Adobe", "Firefly 3", 9.2, "Included in Creative Cloud $55/mo", "Commercially safe AI image generation."),
            createAiTool(4, "Stable Diffusion", "Stability AI", "SDXL / SD3", 8.9, "Free (open source), $20/mo DreamStudio", "Open-source model with unlimited customization."),
            createAiTool(5, "Leonardo AI", "Leonardo AI", "Phoenix", 8.6, "Free tier, $12/mo Artisan", "Versatile AI art platform.")
        ));
    }

    private List<AiToolResult> getVideoTools() {
        return new ArrayList<>(List.of(
            createAiTool(1, "Runway", "Runway ML", "Gen-3 Alpha", 9.6, "Free tier, $15/mo Standard", "State-of-the-art video generation."),
            createAiTool(2, "Sora", OPENAI, "Sora", 9.5, "Included in ChatGPT Plus/Pro", "Groundbreaking minute-long video generation."),
            createAiTool(3, "CapCut", "ByteDance", "AI Video Editor", 9.1, "Free tier, $8/mo Pro", "All-in-one video editor."),
            createAiTool(4, "Synthesia", "Synthesia", "Custom Avatar Engine", 8.8, "$29/mo Starter, $89/mo Creator", "AI avatar video generation."),
            createAiTool(5, "Pika", "Pika Labs", "Pika 1.5", 8.5, "Free tier, $10/mo Standard", "Creative AI video platform.")
        ));
    }

    private List<AiToolResult> getPresentationTools() {
        return new ArrayList<>(List.of(
            createAiTool(1, "Gamma", "Gamma Tech", "Gamma AI", 9.5, "Free tier, $10/mo Plus", "AI-native presentation builder."),
            createAiTool(2, "Beautiful.ai", "Beautiful.ai", "DesignerBot", 9.2, "$12/mo Pro, $40/mo Team", "Smart slide design."),
            createAiTool(3, "Canva (Magic Design)", "Canva", "Magic Design AI", 9.0, "Free tier, $13/mo Pro", "AI-powered templates."),
            createAiTool(4, "Tome", "Tome", "Tome AI", 8.7, "Free tier, $20/mo Professional", "AI-first storytelling platform."),
            createAiTool(5, "SlidesAI", "SlidesAI", "GPT-based", 8.3, "Free tier, $10/mo Pro", "Google Slides plugin.")
        ));
    }

    private List<AiToolResult> getMusicTools() {
        return new ArrayList<>(List.of(
            createAiTool(1, "Suno", "Suno Inc.", "Suno V4", 9.6, "Free tier, $10/mo Pro", "Full song generation."),
            createAiTool(2, "Udio", "Udio", "Udio V1.5", 9.3, "Free tier, $10/mo Standard", "High-fidelity AI music generation."),
            createAiTool(3, "AIVA", "AIVA Technologies", "AIVA Composer", 8.9, "Free tier, $15/mo Standard", "AI composer for cinematic music."),
            createAiTool(4, "Mubert", "Mubert", "Mubert Render", 8.5, "Free tier, $14/mo Creator", "AI-generated royalty-free music."),
            createAiTool(5, "ElevenLabs", "ElevenLabs", "Multilingual V2", 9.4, "Free tier, $5/mo Starter", "Industry-leading AI voice synthesis.")
        ));
    }

    private List<AiToolResult> getResearchTools() {
        return new ArrayList<>(List.of(
            createAiTool(1, CHATGPT, OPENAI, GPT_4O, 9.7, FREE_TIER_20_PLUS, "Most versatile AI assistant."),
            createAiTool(2, "Perplexity", "Perplexity AI", "Sonar Pro", 9.5, FREE_TIER_20_PRO, "AI-powered research engine."),
            createAiTool(3, CLAUDE, ANTHROPIC, CLAUDE_SONNET, 9.4, FREE_TIER_20_PRO, "Exceptional for nuanced research."),
            createAiTool(4, "Gemini", GOOGLE, GEMINI_PRO, 9.1, "Free tier, $20/mo Advanced", "Google's multimodal AI."),
            createAiTool(5, "You.com", "You.com", "You Chat", 8.6, "Free tier, $15/mo YouPro", "Privacy-focused AI search assistant.")
        ));
    }

    private AiToolResult createAiTool(int rank, String name, String provider, String model, double score, String price, String desc) {
        AiToolResult res = new AiToolResult();
        res.setRank(rank);
        res.setToolName(name);
        res.setProvider(provider);
        res.setModelNumber(model);
        res.setScore(score);
        res.setPricing(price);
        res.setDescription(desc);
        return res;
    }
}
