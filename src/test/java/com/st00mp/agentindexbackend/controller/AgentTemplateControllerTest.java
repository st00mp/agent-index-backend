package com.st00mp.agentindexbackend.controller;

import com.st00mp.agentindexbackend.dto.CreateTemplateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AgentTemplateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTemplate_nominal_returns201() throws Exception {
        // Given
        CreateTemplateRequest request = new CreateTemplateRequest(
                "Quote Agent",
                "Sales",
                "Generates a personalised quote based on the company name and hourly rate.",
                "You are a quote assistant for {{company_name}}. Always base your estimates on an hourly rate of {{hourly_rate}} €/h and present them in a clear, professional format.",
                "[{\"key\":\"company_name\",\"label\":\"Company name\",\"type\":\"text\",\"help\":\"\"},{\"key\":\"hourly_rate\",\"label\":\"Hourly rate\",\"type\":\"number\",\"help\":\"e.g. 65\"}]",
                "1.0.0"
        );
        String body = objectMapper.writeValueAsString(request);

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
        CreateTemplateRequest request = new CreateTemplateRequest(
                "",
                "Sales",
                "Generates a personalised quote based on the company name and hourly rate.",
                "You are a quote assistant for {{company_name}}. Always base your estimates on an hourly rate of {{hourly_rate}} €/h and present them in a clear, professional format.",
                "[{\"key\":\"company_name\",\"label\":\"Company name\",\"type\":\"text\",\"help\":\"\"},{\"key\":\"hourly_rate\",\"label\":\"Hourly rate\",\"type\":\"number\",\"help\":\"e.g. 65\"}]",
                "1.0.0"
        );
        String body = objectMapper.writeValueAsString(request);

        // When
        mockMvc.perform(post("/templates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        // Then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists());
    }

}