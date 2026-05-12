package com.cloudcompare.ai.integration.aws;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Extension point for future AWS service and region metadata synchronization.
 * Disabled by default so existing static metadata remains the source of truth.
 */
@Service
public class AwsMetadataAdapter {

    private final AwsIntegrationProperties properties;

    public AwsMetadataAdapter(AwsIntegrationProperties properties) {
        this.properties = properties;
    }

    public boolean isEnabled() {
        return properties.isMetadataEnabled();
    }

    public List<String> getConfiguredRegions() {
        if (!isEnabled()) {
            return List.of();
        }

        return List.of(properties.getRegion());
    }
}
