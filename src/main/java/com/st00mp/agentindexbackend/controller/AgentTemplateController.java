package com.st00mp.agentindexbackend.controller;

import com.st00mp.agentindexbackend.dto.CreateTemplateRequest;
import com.st00mp.agentindexbackend.entity.AgentTemplate;
import com.st00mp.agentindexbackend.service.AgentTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class AgentTemplateController {

    private final AgentTemplateService agentTemplateService;

    public AgentTemplateController(AgentTemplateService agentTemplateService) {
        this.agentTemplateService = agentTemplateService;
    }

    @Operation(
            summary = "Create an agent template",
            description = "Creates a new agent template and returns the created resource with its URL in the Location header."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Template successfully created",
                    headers = @Header(name = "Location", description = "URL of the created resource, e.g. /templates/42")),
            @ApiResponse(responseCode = "400", description = "Invalid request body (missing or blank field)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "object", example = "{\"name\":\"must not be blank\",\"version\":\"must not be blank\"}")))
    })
    @PostMapping("/templates")
    public ResponseEntity<AgentTemplate> createTemplate(@Valid @RequestBody CreateTemplateRequest request) {

        AgentTemplate saved = agentTemplateService.create(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(saved);
    }
}
