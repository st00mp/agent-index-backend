package com.st00mp.agentindexbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record InstanceRequest(

        @Schema(
                description = """
                        Values bound to the template's field keys. Each key must match a field \
                        declared by the template; the example keys correspond to the template's example fields.""",
                example = """
                        {"company_name": "Acme", "hourly_rate": "65"}"""
        )
        @NotNull Map<String, String> values
) {
}
