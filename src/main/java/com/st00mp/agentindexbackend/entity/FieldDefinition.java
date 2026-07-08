package com.st00mp.agentindexbackend.entity;

import jakarta.validation.constraints.NotBlank;

public record FieldDefinition(
        @NotBlank String key,
        @NotBlank String label,
        @NotBlank String type,
        String help
) {
}
