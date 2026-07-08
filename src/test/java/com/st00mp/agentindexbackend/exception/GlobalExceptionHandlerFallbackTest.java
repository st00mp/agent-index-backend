package com.st00mp.agentindexbackend.exception;

import com.st00mp.agentindexbackend.service.AssemblyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerFallbackTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AssemblyService assemblyService;

    @Test
    void unmappedException_isHandledAsControlledJson500() throws Exception {
        // Given: an unexpected, unmapped exception escapes the service layer
        when(assemblyService.assembleOutput(anyLong()))
                .thenThrow(new IllegalStateException("boom"));

        // When / Then: the catch-all turns it into a controlled JSON 500, not a bare 500
        mockMvc.perform(get("/instances/{id}/output", 1L))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void standardMvcException_keepsItsStatus_notSwallowedBy500() throws Exception {
        // Regression guard: the Exception.class catch-all must not hijack Spring's
        // standard MVC exceptions. PATCH on a GET/PUT/DELETE route stays 405, not 500.
        mockMvc.perform(patch("/templates/{id}", 1L))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.error").exists());
    }
}
