package com.st00mp.agentindexbackend.controller;

import com.st00mp.agentindexbackend.dto.InstanceRequest;
import com.st00mp.agentindexbackend.entity.AgentInstance;
import com.st00mp.agentindexbackend.service.AgentInstanceService;
import com.st00mp.agentindexbackend.service.AssemblyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Agent Instances", description = "Create agent instances from templates and assemble their output")
@RestController
public class AgentInstanceController {

    private final AgentInstanceService agentInstanceService;
    private final AssemblyService assemblyService;

    public AgentInstanceController(AgentInstanceService agentInstanceService, AssemblyService assemblyService) {
        this.agentInstanceService = agentInstanceService;
        this.assemblyService = assemblyService;
    }

    @Operation(
            summary = "Create an agent instance",
            description = "Creates a new agent instance from the template identified by templateId and returns the created resource with its URL in the Location header. All fields declared as required by the template must be provided with a non-blank value."
    )
    @ApiResponse(responseCode = "201", description = "Instance successfully created",
            headers = @Header(name = "Location", description = "URL of the created resource, e.g. /instances/42"),
            content = @Content(schema = @Schema(implementation = AgentInstance.class)))
    @PostMapping("/templates/{templateId}/instances")
    public ResponseEntity<AgentInstance> createInstance(
            @Parameter(description = "ID of the template to instantiate", example = "42") @PathVariable Long templateId,
            @Valid @RequestBody InstanceRequest request) {

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

    @Operation(
            summary = "Get the assembled output of an agent instance",
            description = "Assembles the instructions of the instance's template by replacing each {{placeholder}} with the instance's stored values, and returns the result as plain text."
    )
    @ApiResponse(responseCode = "200", description = "Output successfully assembled",
            content = @Content(mediaType = "text/plain",
                    schema = @Schema(type = "string", example = "You are a helpful assistant with a formal tone.")))
    @ApiResponse(responseCode = "422", description = "One or more placeholders could not be resolved",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(type = "object", example = "{\"error\":\"Unresolved placeholders (no value provided): [tone, name]\"}")))
    @GetMapping(value = "/instances/{id}/output", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getInstanceOutput(
            @Parameter(description = "ID of the instance", example = "42") @PathVariable Long id) {
        String output = assemblyService.assembleOutput(id);
        return ResponseEntity.ok(output);
    }

    @Operation(
            summary = "Get an agent instance by ID",
            description = "Returns the agent instance identified by id, including its template reference and stored field values."
    )
    @ApiResponse(responseCode = "200", description = "Instance found",
            content = @Content(schema = @Schema(implementation = AgentInstance.class)))
    @GetMapping("/instances/{id}")
    public ResponseEntity<AgentInstance> getInstanceById(
            @Parameter(description = "ID of the instance", example = "42") @PathVariable Long id) {
        AgentInstance instance = agentInstanceService.getById(id);
        return ResponseEntity.ok(instance);
    }
}
