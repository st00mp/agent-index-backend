package com.st00mp.agentindexbackend.service;

import com.st00mp.agentindexbackend.dto.CreateTemplateRequest;
import com.st00mp.agentindexbackend.entity.AgentTemplate;
import com.st00mp.agentindexbackend.repository.AgentTemplateRepository;
import org.springframework.stereotype.Service;

@Service
public class AgentTemplateService {

    private final AgentTemplateRepository agentTemplateRepository;

    public AgentTemplateService (AgentTemplateRepository  agentTemplateRepository) {
        this.agentTemplateRepository = agentTemplateRepository;
    }

    public AgentTemplate create(CreateTemplateRequest request) {
        AgentTemplate template = new AgentTemplate();
        template.setName(request.name());
        template.setCategory(request.category());
        template.setDescription(request.description());
        template.setInstructions(request.instructions());
        template.setFields(request.fields());
        template.setVersion(request.version());
        return agentTemplateRepository.save(template);
    }

}
