package com.st00mp.agentindexbackend.controller;

import com.st00mp.agentindexbackend.dto.InstanceRequest;
import com.st00mp.agentindexbackend.entity.AgentInstance;
import com.st00mp.agentindexbackend.service.AgentInstanceService;
import com.st00mp.agentindexbackend.service.AssemblyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

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

    @Operation(
            summary = "Get the assembled output of an agent instance",
            description = "Assembles the instructions of the instance's template by replacing each {{placeholder}} with the instance's stored values, and returns the result as plain text."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Output successfully assembled",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "You are a helpful assistant with a formal tone."))),
            @ApiResponse(responseCode = "404", description = "No instance exists with the given ID",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "object", example = "{\"error\":\"AgentInstance 42 not found\"}"))),
            @ApiResponse(responseCode = "422", description = "One or more placeholders could not be resolved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "object", example = "{\"error\":\"Unresolved placeholders (no value provided): [tone, name]\"}")))
    })
    @GetMapping(value = "/instances/{id}/output", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getInstanceOutput(@PathVariable Long id) {
        String output = assemblyService.assembleOutput(id);
        return ResponseEntity.ok(output);
    }

    @Operation(
            summary = "Get an agent instance by ID",
            description = "Returns the agent instance identified by id, including its template reference and stored field values."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Instance found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AgentInstance.class))),
            @ApiResponse(responseCode = "404", description = "No instance exists with the given ID",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "object", example = "{\"error\":\"AgentInstance 42 not found\"}")))
    })
    @GetMapping("/instances/{id}")
    public ResponseEntity<AgentInstance> getInstanceById(@PathVariable Long id) {
        AgentInstance instance = agentInstanceService.getById(id);
        return ResponseEntity.ok(instance);
    }
}
