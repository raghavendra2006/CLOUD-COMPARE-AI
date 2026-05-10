package com.cloudcompare.ai.controller;

import com.cloudcompare.ai.dto.CompareRequest;
import com.cloudcompare.ai.service.CacheService;
import com.cloudcompare.ai.service.GrokClientService;
import com.cloudcompare.ai.service.MetaDataService;
import com.cloudcompare.ai.service.RankingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GrokClientService grokClientService;

    @MockBean
    private MetaDataService metaDataService;

    @MockBean
    private CacheService cacheService;

    @MockBean
    private RankingService rankingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "test@example.com")
    void testCompareEndpoint() throws Exception {
        CompareRequest request = new CompareRequest();
        request.setCategory("compute");
        request.setServiceType("all");
        request.setHours(730);
        request.setRegion("us-east-1");
        request.setPriority("balanced");

        when(grokClientService.fetchComparisonFromGrok(anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        
        when(rankingService.buildResponse(any(), anyString(), anyString(), anyInt(), anyString(), anyInt(), anyString()))
                .thenReturn(new com.cloudcompare.ai.dto.CompareResponse());

        mockMvc.perform(post("/api/compare")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testCompareEndpointRequiresAuth() throws Exception {
        CompareRequest request = new CompareRequest();
        request.setCategory("compute");

        mockMvc.perform(post("/api/compare")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testHealthCheckIsPublic() throws Exception {
        mockMvc.perform(get("/api/test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testCompareEndpointException() throws Exception {
        CompareRequest request = new CompareRequest();
        request.setCategory("compute");
        request.setServiceType("all");

        when(metaDataService.getDefaultServiceType(anyString())).thenReturn("Virtual Machines");
        when(cacheService.get(anyString())).thenReturn(null);
        when(grokClientService.fetchComparisonFromGrok(anyString(), anyString()))
                .thenThrow(new java.io.IOException("YOUR_GROQ_API_KEYS_HERE Error"));

        mockMvc.perform(post("/api/compare")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("AI Engine Configuration Missing. Please set the GROK_API_KEYS environment variable to enable live analysis."));
    }

    @Test
    void testGetServiceTypes() throws Exception {
        when(metaDataService.getServiceTypes(anyString())).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/service-types/compute"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testGetRegions() throws Exception {
        when(metaDataService.getRegions()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/regions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testCompareAiTools() throws Exception {
        com.cloudcompare.ai.dto.AiCompareRequest req = new com.cloudcompare.ai.dto.AiCompareRequest();
        req.setPurpose("testing");

        when(cacheService.get(anyString())).thenReturn(null);
        com.cloudcompare.ai.dto.AiToolResult tool = new com.cloudcompare.ai.dto.AiToolResult();
        tool.setScore(9.5);
        when(grokClientService.fetchAiToolsComparisonFromGrok(anyString()))
                .thenReturn(new java.util.ArrayList<>(java.util.List.of(tool)));

        mockMvc.perform(post("/api/ai-compare")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void testCompareAiToolsException() throws Exception {
        com.cloudcompare.ai.dto.AiCompareRequest req = new com.cloudcompare.ai.dto.AiCompareRequest();
        req.setPurpose("testing");

        when(cacheService.get(anyString())).thenReturn(null);
        when(grokClientService.fetchAiToolsComparisonFromGrok(anyString()))
                .thenThrow(new java.io.IOException("Groq error"));

        mockMvc.perform(post("/api/ai-compare")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.success").value(false));
    }
}
