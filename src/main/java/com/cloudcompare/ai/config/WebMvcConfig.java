package com.cloudcompare.ai.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Serves the React SPA for frontend routes while keeping the legacy static files.
 * React build output is placed in /static/react/ during the CI/CD build.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve React build assets (JS, CSS, images)
        registry.addResourceHandler("/app/**")
                .addResourceLocations("classpath:/static/react/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // SPA fallback: forward all React routes to the React index.html
        registry.addViewController("/app").setViewName("forward:/react/index.html");
        registry.addViewController("/app/").setViewName("forward:/react/index.html");
        registry.addViewController("/app/login").setViewName("forward:/react/index.html");
        registry.addViewController("/app/signup").setViewName("forward:/react/index.html");
        registry.addViewController("/app/dashboard").setViewName("forward:/react/index.html");
    }
}
