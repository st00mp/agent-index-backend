package com.st00mp.agentindexbackend.controller;

import com.st00mp.agentindexbackend.dto.TemplateRequest;
import com.st00mp.agentindexbackend.service.AgentTemplateService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class AgentTemplateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AgentTemplateService agentTemplateService;

    @Nested
    public class CreateTemplateTests {

        @Test
        void createTemplate_nominal_returns201() throws Exception {
            // Given
            var request = new TemplateRequest(
                    "Quote Agent",
                    "Sales",
                    "Generates a personalised quote based on the company name and hourly rate.",
                    "You are a quote assistant for {{company_name}}. Always base your estimates on an hourly rate of {{hourly_rate}} €/h and present them in a clear, professional format.",
                    "[{\"key\":\"company_name\",\"label\":\"Company name\",\"type\":\"text\",\"help\":\"\"},{\"key\":\"hourly_rate\",\"label\":\"Hourly rate\",\"type\":\"number\",\"help\":\"e.g. 65\"}]",
                    "1.0.0"
            );
            var body = objectMapper.writeValueAsString(request);

            // When
            mockMvc.perform(post("/templates")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))

                    // Then
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.name").value("Quote Agent"));
        }

        @Test
        void createTemplate_blankName_returns400() throws Exception {
            // Given
            var request = new TemplateRequest(
                    "",
                    "Sales",
                    "Generates a personalised quote based on the company name and hourly rate.",
                    "You are a quote assistant for {{company_name}}. Always base your estimates on an hourly rate of {{hourly_rate}} €/h and present them in a clear, professional format.",
                    "[{\"key\":\"company_name\",\"label\":\"Company name\",\"type\":\"text\",\"help\":\"\"},{\"key\":\"hourly_rate\",\"label\":\"Hourly rate\",\"type\":\"number\",\"help\":\"e.g. 65\"}]",
                    "1.0.0"
            );
            var body = objectMapper.writeValueAsString(request);

            // When
            mockMvc.perform(post("/templates")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    // Then
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.name").exists());
        }
    }

    @Nested
    class GetTemplateTests {

        @Test
        void getTemplate_nominal_returns200() throws Exception {
            // Given
            var request = new TemplateRequest(
                    "Quote Agent",
                    "Sales",
                    "Generates a personalised quote based on the company name and hourly rate.",
                    "You are a quote assistant for {{company_name}}. Always base your estimates on an hourly rate of {{hourly_rate}} €/h and present them in a clear, professional format.",
                    "[{\"key\":\"company_name\",\"label\":\"Company name\",\"type\":\"text\",\"help\":\"\"},{\"key\":\"hourly_rate\",\"label\":\"Hourly rate\",\"type\":\"number\",\"help\":\"e.g. 65\"}]",
                    "1.0.0"
            );
            var seeded = agentTemplateService.create(request);
            var id = seeded.getId();

            // When/ Then
            mockMvc.perform(get("/templates/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id))
                    .andExpect(jsonPath("$.name").value(seeded.getName()));
        }

        @Test
        void getTemplate_missingId_returns404() throws Exception {
            // Given: no template exists with this id

            // When/ Then
            mockMvc.perform(get("/templates/{id}", 999999L))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").exists());
        }
    }

    @Test
    void getAllTemplates_returns200AndArray() throws Exception {
        // When / Then
        mockMvc.perform(get("/templates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Nested
    class UpdateTemplateTests {

        @Test
        void updateTemplate_nominal_returns200() throws Exception {
            // Given
            var seed = new TemplateRequest(
                    "Quote Agent",
                    "Sales",
                    "Generates a personalised quote based on the company name and hourly rate.",
                    "You are a quote assistant for {{company_name}}. Always base your estimates on an hourly rate of {{hourly_rate}} €/h and present them in a clear, professional format.",
                    "[{\"key\":\"company_name\",\"label\":\"Company name\",\"type\":\"text\",\"help\":\"\"},{\"key\":\"hourly_rate\",\"label\":\"Hourly rate\",\"type\":\"number\",\"help\":\"e.g. 65\"}]",
                    "1.0.0"
            );
            var id = agentTemplateService.create(seed).getId();

            var update = new TemplateRequest(
                    "Quote Agent",
                    "Sales",
                    "Generates a personalised quote based on the company name and hourly rate.",
                    "You are a quote assistant for {{company_name}}. Always base your estimates on an hourly rate of {{hourly_rate}} €/h and present them in a clear, professional format.",
                    "[{\"key\":\"company_name\",\"label\":\"Company name\",\"type\":\"text\",\"help\":\"\"},{\"key\":\"hourly_rate\",\"label\":\"Hourly rate\",\"type\":\"number\",\"help\":\"e.g. 65\"}]",
                    "2.0.0"
            );
            var body = objectMapper.writeValueAsString(update);

            // When
            mockMvc.perform(put("/templates/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    // Then
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id))
                    .andExpect(jsonPath("$.version").value(update.version()));
        }

        @Test
        void updateTemplate_missingId_returns404() throws Exception {
            // Given: no template exists with this id
            var update = new TemplateRequest(
                    "Quote Agent",
                    "Sales",
                    "Generates a personalised quote based on the company name and hourly rate.",
                    "You are a quote assistant for {{company_name}}. Always base your estimates on an hourly rate of {{hourly_rate}} €/h and present them in a clear, professional format.",
                    "[{\"key\":\"company_name\",\"label\":\"Company name\",\"type\":\"text\",\"help\":\"\"},{\"key\":\"hourly_rate\",\"label\":\"Hourly rate\",\"type\":\"number\",\"help\":\"e.g. 65\"}]",
                    "1.0.0"
            );
            var body = objectMapper.writeValueAsString(update);

            // When/ Then
            mockMvc.perform(put("/templates/{id}", 999999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    // Then
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").exists());
        }

        @Test
        void updateTemplate_blankName_returns400() throws Exception {
            // Given: invalid body (name blank)
            var invalid = new TemplateRequest(
                    "",
                    "Sales",
                    "Generates a personalised quote based on the company name and hourly rate.",
                    "You are a quote assistant for {{company_name}}. Always base your estimates on an hourly rate of {{hourly_rate}} €/h and present them in a clear, professional format.",
                    "[{\"key\":\"company_name\",\"label\":\"Company name\",\"type\":\"text\",\"help\":\"\"},{\"key\":\"hourly_rate\",\"label\":\"Hourly rate\",\"type\":\"number\",\"help\":\"e.g. 65\"}]",
                    "1.0.0"
            );
            var body = objectMapper.writeValueAsString(invalid);

            // When / Then
            mockMvc.perform(put("/templates/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.name").exists());
        }
    }

    @Nested
    class DeleteTemplateTests {

        @Test
        void deleteTemplate_nominal_returns204() throws Exception {
            // Given
            var seed = new TemplateRequest(
                    "Quote Agent",
                    "Sales",
                    "Generates a personalised quote based on the company name and hourly rate.",
                    "You are a quote assistant for {{company_name}}. Always base your estimates on an hourly rate of {{hourly_rate}} €/h and present them in a clear, professional format.",
                    "[{\"key\":\"company_name\",\"label\":\"Company name\",\"type\":\"text\",\"help\":\"\"},{\"key\":\"hourly_rate\",\"label\":\"Hourly rate\",\"type\":\"number\",\"help\":\"e.g. 65\"}]",
                    "1.0.0"
            );
            var id = agentTemplateService.create(seed).getId();

            // When/ Then
            mockMvc.perform(delete("/templates/{id}", id))
                    .andExpect(status().isNoContent());
            mockMvc.perform(get("/templates/{id}", id))
                    .andExpect(status().isNotFound());
        }

        @Test
        void deleteTemplate_missingId_returns404() throws Exception {
            // Given: no template exists with this id

            // When/ Then
            mockMvc.perform(delete("/templates/{id}", 999999L))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").exists());
        }
    }
}
