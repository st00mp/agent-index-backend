package com.st00mp.agentindexbackend.controller;

import com.st00mp.agentindexbackend.dto.InstanceRequest;
import com.st00mp.agentindexbackend.dto.TemplateRequest;
import com.st00mp.agentindexbackend.entity.AgentInstance;
import com.st00mp.agentindexbackend.repository.AgentInstanceRepository;
import com.st00mp.agentindexbackend.service.AgentInstanceService;
import com.st00mp.agentindexbackend.service.AgentTemplateService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AgentInstanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AgentTemplateService agentTemplateService;

    @Autowired
    private AgentInstanceService agentInstanceService;

    @Autowired
    private AgentInstanceRepository agentInstanceRepository;

    @Nested
    public class CreateInstanceTests {

        @Test
        void createInstance_nominal_returns201() throws Exception {
            // Given
            var templateRequest = new TemplateRequest(
                    "Quote Agent",
                    "Sales",
                    "Generates a personalised quote based on the company name and hourly rate.",
                    "You are a quote assistant for {{company_name}}.",
                    "[{\"key\":\"company_name\"}]",
                    "1.0.0"
            );
            var templateId = agentTemplateService.create(templateRequest).getId();

            var instanceRequest = new InstanceRequest(
                    Map.of("company_name", "Fiduciaire Horizon"));

            // When
            mockMvc.perform(post("/templates/{templateId}/instances", templateId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(instanceRequest)))

                    // Then
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.templateId").value(templateId))
                    .andExpect(jsonPath("$.values.company_name").value("Fiduciaire Horizon"));
        }

        @Test
        void createInstance_blankValue_returns400() throws Exception {
            // Given
            var templateRequest = new TemplateRequest(
                    "Quote Agent", "Sales", "desc",
                    "Instructions {{company_name}}",
                    "[{\"key\":\"company_name\"}]",
                    "1.0.0"
            );
            var templateId = agentTemplateService.create(templateRequest).getId();

            var request = new InstanceRequest(Map.of("company_name", "   "));   // blank value

            // When
            mockMvc.perform(post("/templates/{templateId}/instances", templateId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    // Then
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").exists());
        }

        @Test
        void createInstance_incompleteValues_returns400() throws Exception {
            // Given
            var templateRequest = new TemplateRequest(
                    "Quote Agent", "Sales", "desc",
                    "Instructions {{company_name}}",
                    "[{\"key\":\"company_name\"}]",
                    "1.0.0"
            );
            var templateId = agentTemplateService.create(templateRequest).getId();

            var request = new InstanceRequest(Map.of());   // company_name missing

            // When
            mockMvc.perform(post("/templates/{templateId}/instances", templateId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    // Then
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").exists());
        }

        @Test
        void createInstance_missingValues_returns400() throws Exception {
            // Given
            var invalid = new InstanceRequest(null);
            var body = objectMapper.writeValueAsString(invalid);

            // When
            mockMvc.perform(post("/templates/{id}/instances", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    // Then
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.values").exists());
        }

        @Test
        void createInstance_unknownTemplate_returns404() throws Exception {
            // Given
            var request = new InstanceRequest(Map.of("company_name", "Fiduciaire Horizon"));

            // When
            mockMvc.perform(post("/templates/{id}/instances", 999999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    // Then
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").exists());
        }
    }

    @Nested
    public class GetInstanceOutputTests {

        @Test
        void getInstanceOutput_nominal_returns200() throws Exception {
            // Given
            var templateId = agentTemplateService.create(new TemplateRequest(
                    "Quote Agent",
                    "Sales",
                    "desc",
                    "You are the assistant for {{company_name}}.",
                    "[{\"key\":\"company_name\"}]",
                    "1.0.0")).getId();

            var instanceId = agentInstanceService.create(templateId,
                    new InstanceRequest(Map.of("company_name", "Fiduciaire Horizon"))).getId();

            // When
            mockMvc.perform(get("/instances/{id}/output", instanceId))
                    // Then
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                    .andExpect(content().string("You are the assistant for Fiduciaire Horizon."));
        }

        @Test
        void getInstanceOutput_unknownInstance_returns404() throws Exception {
            mockMvc.perform(get("/instances/{id}/output", 999999L))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").exists());
        }

        @Test
        void getInstanceOutput_unresolvedPlaceholder_returns422() throws Exception {
            // Given
            var templateId = agentTemplateService.create(new TemplateRequest(
                    "Quote Agent", "Sales", "desc",
                    "Hi {{company_name}}, signed {{signature}}.",   // 2 placeholders
                    "[{\"key\":\"company_name\"}]",                  // fields : signature ABSENT
                    "1.0.0")).getId();

            var instanceId = agentInstanceService.create(templateId,
                    new InstanceRequest(Map.of("company_name", "Fiduciaire Horizon"))).getId();

            // When
            mockMvc.perform(get("/instances/{id}/output", instanceId))
                    // Then
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.error").exists());
        }

        @Test
        void getInstanceOutput_unknownTemplate_returns404() throws Exception {
            // Given
            var orphan = new AgentInstance();
            orphan.setTemplateId(999999L);
            orphan.setValues(Map.of("company_name", "Fiduciaire Horizon"));
            var instanceId = agentInstanceRepository.save(orphan).getId();

            // When
            mockMvc.perform(get("/instances/{id}/output", instanceId))
                    // Then
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").exists());
        }
    }

    @Nested
    public class GetInstanceByIdTests {

        @Test
        void getInstanceById_nominal_returns200() throws Exception {
            // Given
            var templateId = agentTemplateService.create(new TemplateRequest(
                    "Quote Agent",
                    "Sales",
                    "desc",
                    "You are the assistant for {{company_name}}.",
                    "[{\"key\":\"company_name\"}]",
                    "1.0.0")).getId();

            var instanceId = agentInstanceService.create(templateId,
                    new InstanceRequest(Map.of("company_name", "Fiduciaire Horizon"))).getId();

            // When
            mockMvc.perform(get("/instances/{id}", instanceId))
                    // Then
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(instanceId))
                    .andExpect(jsonPath("$.templateId").value(templateId))
                    .andExpect(jsonPath("$.values.company_name").value("Fiduciaire Horizon"));
        }

        @Test
        void getInstanceById_unknownInstance_returns404() throws Exception {
            mockMvc.perform(get("/instances/{id}", 999999L))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").exists());
        }
    }
}
