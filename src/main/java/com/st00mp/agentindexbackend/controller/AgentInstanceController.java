package com.st00mp.agentindexbackend.controller;

import com.st00mp.agentindexbackend.dto.InstanceRequest;
import com.st00mp.agentindexbackend.entity.AgentInstance;
import com.st00mp.agentindexbackend.service.AgentInstanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class AgentInstanceController {

    private final AgentInstanceService agentInstanceService;

    public AgentInstanceController(AgentInstanceService agentInstanceService) {
        this.agentInstanceService = agentInstanceService;
    }

    @Operation(
            summary = "Create an agent instance",
            description = "Creates a new agent instance from the template identified by templateId and returns the created resource with its URL in the Location header. All fields declared as required by the template must be provided with a non-blank value."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Instance successfully created",
                    headers = @Header(name = "Location", description = "URL of the created resource, e.g. /instances/42")),
            @ApiResponse(responseCode = "400", description = "Invalid request body (missing values field) or one or more required field values are missing or blank",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "object", example = "{\"error\":\"Missing or empty values for required fields: [name, version]\"}"))),
            @ApiResponse(responseCode = "404", description = "No template exists with the given ID",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "object", example = "{\"error\":\"AgentTemplate 42 not found\"}")))
    })
    @PostMapping("/templates/{templateId}/instances")
    public ResponseEntity<AgentInstance> createInstance(@PathVariable Long templateId, @Valid @RequestBody InstanceRequest request) {

        AgentInstance saved = agentInstanceService.create(templateId, request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/instances/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(saved);
    }
}
