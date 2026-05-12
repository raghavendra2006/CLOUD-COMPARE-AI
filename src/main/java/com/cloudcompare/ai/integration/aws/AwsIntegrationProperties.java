package com.cloudcompare.ai.integration.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Disabled-by-default AWS adapter flags.
 * These keep optional AWS integrations additive and prevent local/mock flows from changing.
 */
@Component
public class AwsIntegrationProperties {

    private final boolean pricingEnabled;
    private final boolean metadataEnabled;
    private final String region;

    public AwsIntegrationProperties(
            @Value("${cloudcompare.aws.pricing.enabled:false}") boolean pricingEnabled,
            @Value("${cloudcompare.aws.metadata.enabled:false}") boolean metadataEnabled,
            @Value("${cloudcompare.aws.region:us-east-1}") String region) {
        this.pricingEnabled = pricingEnabled;
        this.metadataEnabled = metadataEnabled;
        this.region = region;
    }

    public boolean isPricingEnabled() {
        return pricingEnabled;
    }

    public boolean isMetadataEnabled() {
        return metadataEnabled;
    }

    public String getRegion() {
        return region;
    }
}
