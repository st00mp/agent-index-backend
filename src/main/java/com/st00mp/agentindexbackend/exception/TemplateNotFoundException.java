package com.st00mp.agentindexbackend.exception;

public class TemplateNotFoundException extends RuntimeException {
    public TemplateNotFoundException(Long id) {
        super("AgentTemplate %d not found".formatted(id));
    }
}
