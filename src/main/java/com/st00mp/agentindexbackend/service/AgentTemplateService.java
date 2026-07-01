package com.st00mp.agentindexbackend.service;

import com.st00mp.agentindexbackend.dto.TemplateRequest;
import com.st00mp.agentindexbackend.entity.AgentTemplate;
import com.st00mp.agentindexbackend.exception.TemplateNotFoundException;
import com.st00mp.agentindexbackend.repository.AgentTemplateRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgentTemplateService {

    private final AgentTemplateRepository agentTemplateRepository;

    public AgentTemplateService (AgentTemplateRepository  agentTemplateRepository) {
        this.agentTemplateRepository = agentTemplateRepository;
    }

    public AgentTemplate create(TemplateRequest request) {
        AgentTemplate template = new AgentTemplate();
        template.setName(request.name());
        template.setCategory(request.category());
        template.setDescription(request.description());
        template.setInstructions(request.instructions());
        template.setFields(request.fields());
        template.setVersion(request.version());

        return agentTemplateRepository.save(template);
    }

    public AgentTemplate getById(Long id) {
        return agentTemplateRepository.findById(id)
                .orElseThrow(() -> new TemplateNotFoundException(id));
    }

    public List<AgentTemplate> getAll() {
        return agentTemplateRepository.findAll();
    }
}
