package com.st00mp.agentindexbackend.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record InstanceRequest(
        @NotNull Map<String, String> values
) {}
