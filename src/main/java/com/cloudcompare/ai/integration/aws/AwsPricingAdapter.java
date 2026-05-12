package com.cloudcompare.ai.integration.aws;

import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Extension point for future AWS Pricing API integration.
 * Current behavior is intentionally no-op unless the pricing flag is enabled.
 */
@Service
public class AwsPricingAdapter {

    private final AwsIntegrationProperties properties;

    public AwsPricingAdapter(AwsIntegrationProperties properties) {
        this.properties = properties;
    }

    public boolean isEnabled() {
        return properties.isPricingEnabled();
    }

    public Optional<Double> estimateMonthlyCost(String serviceCode, String region, int hours, int storageGb) {
        if (!isEnabled()) {
            return Optional.empty();
        }

        // Placeholder for AWS Pricing API lookup. Returning empty keeps existing Groq/mock pricing unchanged.
        return Optional.empty();
    }
}
