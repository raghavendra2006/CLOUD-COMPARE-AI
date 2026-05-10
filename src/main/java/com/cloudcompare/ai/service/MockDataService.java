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

    public List<Map<String, Object>> getMockComparison(String serviceType) {
        return new ArrayList<>(List.of(
            Map.ofEntries(Map.entry("provider", "AWS"), Map.entry("service_name", "AWS " + serviceType), Map.entry("performance_score", 9.2), Map.entry("popularity_score", 9.8), Map.entry("price_per_hour", 0.05), Map.entry("price_per_gb", 0.0), Map.entry("cpu", 2), Map.entry("ram", 4), Map.entry("storage", 100), Map.entry("region", "us-east-1"), Map.entry("description", "Highly reliable and scalable.")),
            Map.ofEntries(Map.entry("provider", "GCP"), Map.entry("service_name", "Google " + serviceType), Map.entry("performance_score", 9.5), Map.entry("popularity_score", 9.0), Map.entry("price_per_hour", 0.045), Map.entry("price_per_gb", 0.0), Map.entry("cpu", 2), Map.entry("ram", 4), Map.entry("storage", 100), Map.entry("region", "us-central1"), Map.entry("description", "Excellent performance and analytics integration.")),
            Map.ofEntries(Map.entry("provider", "Azure"), Map.entry("service_name", "Azure " + serviceType), Map.entry("performance_score", 9.0), Map.entry("popularity_score", 9.5), Map.entry("price_per_hour", 0.048), Map.entry("price_per_gb", 0.0), Map.entry("cpu", 2), Map.entry("ram", 4), Map.entry("storage", 100), Map.entry("region", "eastus"), Map.entry("description", "Seamless enterprise integration.")),
            Map.ofEntries(Map.entry("provider", "OCI"), Map.entry("service_name", "Oracle " + serviceType), Map.entry("performance_score", 8.8), Map.entry("popularity_score", 7.5), Map.entry("price_per_hour", 0.035), Map.entry("price_per_gb", 0.0), Map.entry("cpu", 2), Map.entry("ram", 4), Map.entry("storage", 100), Map.entry("region", "us-ashburn-1"), Map.entry("description", "Cost-effective for high workloads.")),
            Map.ofEntries(Map.entry("provider", "Alibaba"), Map.entry("service_name", "Alibaba " + serviceType), Map.entry("performance_score", 8.5), Map.entry("popularity_score", 8.0), Map.entry("price_per_hour", 0.038), Map.entry("price_per_gb", 0.0), Map.entry("cpu", 2), Map.entry("ram", 4), Map.entry("storage", 100), Map.entry("region", "ap-southeast-1"), Map.entry("description", "Strong presence in APAC with competitive pricing."))
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

        if (lower.contains("coding") || lower.contains("software") || lower.contains("development")) {
            return new ArrayList<>(List.of(
                createAiTool(1, "GitHub Copilot", "GitHub / Microsoft", "Codex + GPT-4", 9.7, "$10/mo Individual, $19/mo Business", "AI pair programmer that autocompletes code in real-time across all major IDEs. Supports 40+ languages with context-aware suggestions."),
                createAiTool(2, "Cursor", "Anysphere", "GPT-4 / Claude 3.5", 9.5, "Free tier, $20/mo Pro", "AI-native code editor built from VS Code. Features AI chat, code generation, and multi-file editing with full codebase context."),
                createAiTool(3, "Claude (Coding)", "Anthropic", "Claude 3.5 Sonnet", 9.4, "Free tier, $20/mo Pro", "Exceptional at complex reasoning, debugging, and generating production-quality code. 200K context window handles large codebases."),
                createAiTool(4, "ChatGPT (Code Interpreter)", "OpenAI", "GPT-4o", 9.2, "Free tier, $20/mo Plus", "Versatile coding assistant with code execution capability. Excellent for prototyping, debugging, and explaining complex algorithms."),
                createAiTool(5, "Tabnine", "Tabnine", "Custom LLM", 8.6, "Free tier, $12/mo Pro", "Privacy-focused AI code completion that runs locally. Supports all major IDEs with team-trained models for enterprise.")
            ));
        }

        if (lower.contains("content") || lower.contains("writing") || lower.contains("copywriting")) {
            return new ArrayList<>(List.of(
                createAiTool(1, "Claude", "Anthropic", "Claude 3.5 Sonnet", 9.8, "Free tier, $20/mo Pro", "Best-in-class for long-form writing, nuanced tone, and editorial quality. Handles 200K tokens for book-length projects."),
                createAiTool(2, "ChatGPT", "OpenAI", "GPT-4o", 9.5, "Free tier, $20/mo Plus", "Versatile content creation from blog posts to ad copy. Excellent creativity and style adaptation with custom GPTs."),
                createAiTool(3, "Jasper", "Jasper AI", "Multi-model", 9.2, "$49/mo Creator, $125/mo Pro", "Purpose-built for marketing content. Brand voice control, templates for ads, emails, and social media posts."),
                createAiTool(4, "Copy.ai", "Copy.ai", "GPT-4 based", 8.9, "Free tier, $49/mo Pro", "Specialized in short-form marketing copy. One-click generation for product descriptions, headlines, and CTAs."),
                createAiTool(5, "Writesonic", "Writesonic", "GPT-4 based", 8.5, "Free tier, $20/mo Pro", "AI writer with built-in SEO optimization, fact-checking, and real-time web research for content creation.")
            ));
        }

        if (lower.contains("data") || lower.contains("analysis") || lower.contains("spreadsheet")) {
            return new ArrayList<>(List.of(
                createAiTool(1, "ChatGPT (Code Interpreter)", "OpenAI", "GPT-4o", 9.6, "Free tier, $20/mo Plus", "Upload datasets, run Python analysis, generate charts and insights automatically. Best for ad-hoc data exploration."),
                createAiTool(2, "Julius AI", "Julius AI", "Multi-model", 9.3, "Free tier, $20/mo Pro", "Dedicated data analysis AI. Upload CSV/Excel files and get instant visualizations, statistical analysis, and predictive modeling."),
                createAiTool(3, "Claude", "Anthropic", "Claude 3.5 Sonnet", 9.1, "Free tier, $20/mo Pro", "Excellent at interpreting complex datasets, writing SQL queries, and explaining statistical concepts with 200K context."),
                createAiTool(4, "Google Sheets AI (Gemini)", "Google", "Gemini 1.5 Pro", 8.8, "Included in Workspace, $20/mo AI Premium", "Built-in AI for Google Sheets. Natural language formulas, data categorization, and smart chart suggestions."),
                createAiTool(5, "Tableau GPT", "Salesforce", "Einstein GPT", 8.5, "$75/mo Creator", "Enterprise-grade data visualization with AI-driven insights. Natural language queries over complex business datasets.")
            ));
        }

        if (lower.contains("image") || lower.contains("design") || lower.contains("graphic")) {
            return new ArrayList<>(List.of(
                createAiTool(1, "Midjourney", "Midjourney Inc.", "Midjourney V6.1", 9.8, "$10/mo Basic, $30/mo Standard", "Industry-leading image generation with photorealistic quality. Exceptional aesthetics, lighting, and artistic style control."),
                createAiTool(2, "DALL-E 3", "OpenAI", "DALL-E 3", 9.4, "Included in ChatGPT Plus $20/mo", "Highly accurate text-to-image generation with excellent prompt following. Built into ChatGPT for seamless workflow."),
                createAiTool(3, "Adobe Firefly", "Adobe", "Firefly 3", 9.2, "Included in Creative Cloud $55/mo", "Commercially safe AI image generation. Deep integration with Photoshop, Illustrator, and Adobe Express."),
                createAiTool(4, "Stable Diffusion", "Stability AI", "SDXL / SD3", 8.9, "Free (open source), $20/mo DreamStudio", "Open-source model with unlimited customization. Run locally for privacy. Massive community of fine-tuned models."),
                createAiTool(5, "Leonardo AI", "Leonardo AI", "Phoenix", 8.6, "Free tier, $12/mo Artisan", "Versatile AI art platform with real-time canvas, texture generation, and game asset creation tools.")
            ));
        }

        if (lower.contains("video") || lower.contains("editing")) {
            return new ArrayList<>(List.of(
                createAiTool(1, "Runway", "Runway ML", "Gen-3 Alpha", 9.6, "Free tier, $15/mo Standard", "State-of-the-art text/image-to-video generation. Professional motion brush, camera controls, and cinematic quality."),
                createAiTool(2, "Sora", "OpenAI", "Sora", 9.5, "Included in ChatGPT Plus/Pro", "Groundbreaking minute-long video generation from text. Exceptional understanding of physics and motion."),
                createAiTool(3, "CapCut", "ByteDance", "AI Video Editor", 9.1, "Free tier, $8/mo Pro", "All-in-one video editor with AI-powered auto-captions, background removal, and smart editing tools."),
                createAiTool(4, "Synthesia", "Synthesia", "Custom Avatar Engine", 8.8, "$29/mo Starter, $89/mo Creator", "AI avatar video generation for training, marketing, and presentations. 140+ languages with lip-sync."),
                createAiTool(5, "Pika", "Pika Labs", "Pika 1.5", 8.5, "Free tier, $10/mo Standard", "Creative AI video platform for short-form content. Unique style effects and scene generation capabilities.")
            ));
        }

        if (lower.contains("presentation") || lower.contains("slide")) {
            return new ArrayList<>(List.of(
                createAiTool(1, "Gamma", "Gamma Tech", "Gamma AI", 9.5, "Free tier, $10/mo Plus", "AI-native presentation builder. Generates complete slide decks from a single prompt with beautiful, responsive designs."),
                createAiTool(2, "Beautiful.ai", "Beautiful.ai", "DesignerBot", 9.2, "$12/mo Pro, $40/mo Team", "Smart slide design with AI-powered formatting. Automatically adjusts layouts, charts, and animations."),
                createAiTool(3, "Canva (Magic Design)", "Canva", "Magic Design AI", 9.0, "Free tier, $13/mo Pro", "AI-powered presentation templates with brand kit integration. One-click design generation and Magic Write."),
                createAiTool(4, "Tome", "Tome", "Tome AI", 8.7, "Free tier, $20/mo Professional", "AI-first storytelling platform that creates narrative presentations. Embeds live data, 3D, and interactive content."),
                createAiTool(5, "SlidesAI", "SlidesAI", "GPT-based", 8.3, "Free tier, $10/mo Pro", "Google Slides plugin that auto-generates complete presentations from text. Topic-based slide generation with templates.")
            ));
        }

        if (lower.contains("music") || lower.contains("audio")) {
            return new ArrayList<>(List.of(
                createAiTool(1, "Suno", "Suno Inc.", "Suno V4", 9.6, "Free tier, $10/mo Pro", "Full song generation with vocals, lyrics, and instrumentation from text prompts. Multiple genres and styles."),
                createAiTool(2, "Udio", "Udio", "Udio V1.5", 9.3, "Free tier, $10/mo Standard", "High-fidelity AI music generation with exceptional vocal quality. Supports genre mixing and stem separation."),
                createAiTool(3, "AIVA", "AIVA Technologies", "AIVA Composer", 8.9, "Free tier, $15/mo Standard", "AI composer for cinematic and classical music. Licensed for commercial use with full copyright ownership."),
                createAiTool(4, "Mubert", "Mubert", "Mubert Render", 8.5, "Free tier, $14/mo Creator", "AI-generated royalty-free background music for videos, streams, and podcasts. Real-time generation API available."),
                createAiTool(5, "ElevenLabs", "ElevenLabs", "Multilingual V2", 9.4, "Free tier, $5/mo Starter", "Industry-leading AI voice synthesis and cloning. Ultra-realistic text-to-speech in 29 languages with emotion control.")
            ));
        }

        if (lower.contains("research") || lower.contains("chat") || lower.contains("general")) {
            return new ArrayList<>(List.of(
                createAiTool(1, "ChatGPT", "OpenAI", "GPT-4o", 9.7, "Free tier, $20/mo Plus", "Most versatile AI assistant. Excels at research, conversation, coding, writing, and analysis with web browsing and plugins."),
                createAiTool(2, "Perplexity", "Perplexity AI", "Sonar Pro", 9.5, "Free tier, $20/mo Pro", "AI-powered research engine with real-time web search. Provides cited answers, follow-up questions, and source verification."),
                createAiTool(3, "Claude", "Anthropic", "Claude 3.5 Sonnet", 9.4, "Free tier, $20/mo Pro", "Exceptional for nuanced research and complex reasoning. 200K context window enables analysis of entire documents and books."),
                createAiTool(4, "Gemini", "Google", "Gemini 1.5 Pro", 9.1, "Free tier, $20/mo Advanced", "Google's multimodal AI with deep web integration. Excellent at summarizing research papers and cross-referencing sources."),
                createAiTool(5, "You.com", "You.com", "You Chat", 8.6, "Free tier, $15/mo YouPro", "Privacy-focused AI search assistant. Combines multiple AI models with real-time web results for comprehensive research.")
            ));
        }

        // Default fallback for unrecognized purposes
        return getMockAiTools();
    }

    /**
     * Generic fallback mock AI tools (used when purpose is unknown).
     */
    public List<AiToolResult> getMockAiTools() {
        return new ArrayList<>(List.of(
            createAiTool(1, "ChatGPT", "OpenAI", "GPT-4o", 9.8, "Free tier, $20/mo Pro", "Excellent general purpose AI."),
            createAiTool(2, "Claude", "Anthropic", "Claude 3.5 Sonnet", 9.6, "Free tier, $20/mo Pro", "Superior reasoning and writing."),
            createAiTool(3, "Gemini", "Google", "Gemini 1.5 Pro", 9.3, "Free tier, $20/mo Advanced", "Deep integration with Google Workspace."),
            createAiTool(4, "Copilot", "Microsoft", "GPT-4", 9.0, "Included in M365, $30/mo", "Best for enterprise productivity."),
            createAiTool(5, "Perplexity", "Perplexity AI", "Sonar", 8.8, "Free tier, $20/mo Pro", "Outstanding for research and search.")
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
