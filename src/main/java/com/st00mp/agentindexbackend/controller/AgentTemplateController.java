package com.st00mp.agentindexbackend.controller;

import com.st00mp.agentindexbackend.dto.TemplateRequest;
import com.st00mp.agentindexbackend.entity.AgentTemplate;
import com.st00mp.agentindexbackend.service.AgentTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Tag(name = "Agent Templates", description = "Create, read, update and delete reusable agent templates")
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
    @ApiResponse(responseCode = "201", description = "Template successfully created",
            headers = @Header(name = "Location", description = "URL of the created resource, e.g. /templates/42"),
            content = @Content(schema = @Schema(implementation = AgentTemplate.class)))
    @PostMapping("/templates")
    public ResponseEntity<AgentTemplate> createTemplate(@Valid @RequestBody TemplateRequest request) {

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

    @Operation(
            summary = "Get an agent template",
            description = "Returns an agent template by its ID."
    )
    @ApiResponse(responseCode = "200", description = "Template successfully retrieved",
            content = @Content(schema = @Schema(implementation = AgentTemplate.class)))
    @GetMapping("/templates/{id}")
    public ResponseEntity<AgentTemplate> getTemplateById(
            @Parameter(description = "ID of the template", example = "42") @PathVariable Long id) {
        AgentTemplate template = agentTemplateService.getById(id);
        return ResponseEntity.ok(template);
    }

    @Operation(
            summary = "List all agent templates",
            description = "Returns all agent templates. Returns an empty array if no template exists."
    )
    @ApiResponse(responseCode = "200", description = "Templates successfully retrieved",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = AgentTemplate.class))))
    @GetMapping("/templates")
    public ResponseEntity<List<AgentTemplate>> getAllTemplates() {
        List<AgentTemplate> templates = agentTemplateService.getAll();
        return ResponseEntity.ok(templates);
    }

    @Operation(
            summary = "Update an agent template",
            description = "Updates an existing agent template by its ID and returns the updated resource."
    )
    @ApiResponse(responseCode = "200", description = "Template successfully updated",
            content = @Content(schema = @Schema(implementation = AgentTemplate.class)))
    @PutMapping("/templates/{id}")
    public ResponseEntity<AgentTemplate> updateTemplate(
            @Parameter(description = "ID of the template", example = "42") @PathVariable Long id,
            @Valid @RequestBody TemplateRequest request) {
        AgentTemplate updated = agentTemplateService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Delete an agent template",
            description = "Deletes an agent template by its ID."
    )
    @ApiResponse(responseCode = "204", description = "Template successfully deleted")
    @DeleteMapping("/templates/{id}")
    public ResponseEntity<Void> deleteTemplate(
            @Parameter(description = "ID of the template", example = "42") @PathVariable Long id) {
        agentTemplateService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
