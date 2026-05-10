package com.cloudcompare.ai.service;

import com.cloudcompare.ai.dto.Region;
import com.cloudcompare.ai.dto.ServiceType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class MetaDataServiceTest {

    @Autowired
    private MetaDataService metaDataService;

    @Test
    void testGetRegions() {
        List<Region> regions = metaDataService.getRegions();
        assertNotNull(regions);
        assertFalse(regions.isEmpty());
    }

    @Test
    void testGetServiceTypes() {
        List<ServiceType> types = metaDataService.getServiceTypes("compute");
        assertNotNull(types);
        assertFalse(types.isEmpty());
    }

    @Test
    void testGetDefaultServiceType() {
        assertEquals("Virtual Machines", metaDataService.getDefaultServiceType("compute"));
        assertEquals("Object Storage", metaDataService.getDefaultServiceType("storage"));
        assertEquals("Relational Database", metaDataService.getDefaultServiceType("database"));
    }

    @Test
    void testGetServiceTypesInvalidCategory() {
        List<ServiceType> types = metaDataService.getServiceTypes("invalid");
        assertTrue(types.isEmpty());
    }
}
