package com.st00mp.agentindexbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record TemplateRequest(

        @Schema(description = "Name of the agent template", example = "Quote Agent")
        @NotBlank String name,

        @Schema(description = "Functional category of the template", example = "Sales")
        @NotBlank String category,

        @Schema(description = "Short description of the agent's role and capabilities", example = "Generates professional quotes tailored to each client, ready to send.")
        @NotBlank String description,

        @Schema(description = "Detailed system instructions passed to the agent (system prompt)", example = "You are a quote assistant for {{company_name}}. Always base your estimates on an hourly rate of {{hourly_rate}} €/h and present them in a clear, professional format.")
        @NotBlank String instructions,

        @Schema(
                description = "Form fields associated with the template, as a **stringified** JSON array. "
                        + "This field is a plain String: send the JSON array serialized as an escaped string "
                        + "(e.g. \"[{\\\"key\\\":\\\"company_name\\\"}]\"), NOT a raw JSON array. "
                        + "Swagger UI may render the example as a structured array, but the value sent must remain a string.",
                type = "string",
                example = "[{\"key\":\"company_name\",\"label\":\"Company name\",\"type\":\"text\",\"help\":\"\"},{\"key\":\"hourly_rate\",\"label\":\"Hourly rate\",\"type\":\"number\",\"help\":\"e.g. 65\"}]"
        )
        @NotBlank String fields,

        @Schema(description = "Semantic version of the template", example = "1.0.0")
        @NotBlank String version
) {}
