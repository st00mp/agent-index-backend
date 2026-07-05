package com.st00mp.agentindexbackend.exception;

public class InstanceNotFoundException extends RuntimeException {
    public InstanceNotFoundException(Long id) {
        super("AgentInstance %d not found".formatted(id));
    }
}