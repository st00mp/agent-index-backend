package com.st00mp.agentindexbackend.service;

import com.st00mp.agentindexbackend.dto.InstanceRequest;
import com.st00mp.agentindexbackend.entity.AgentInstance;
import com.st00mp.agentindexbackend.entity.AgentTemplate;
import com.st00mp.agentindexbackend.exception.IncompleteInstanceException;
import com.st00mp.agentindexbackend.exception.TemplateNotFoundException;
import com.st00mp.agentindexbackend.repository.AgentInstanceRepository;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.HashSet;
import java.util.Set;

@Service
public class AgentInstanceService {

    private final AgentInstanceRepository agentInstanceRepository;
    private final AgentTemplateService agentTemplateService;
    private final ObjectMapper objectMapper;

    public AgentInstanceService(AgentInstanceRepository agentInstanceRepository,
                                AgentTemplateService agentTemplateService,
                                ObjectMapper objectMapper) {
        this.agentInstanceRepository = agentInstanceRepository;
        this.agentTemplateService = agentTemplateService;
        this.objectMapper = objectMapper;
    }

    /**
     * Creates and persists a new {@link AgentInstance} from a template.
     * <p>
     * The method loads the {@link AgentTemplate} identified by {@code templateId},
     * extracts the set of required field keys declared in the template, and verifies
     * that the incoming request provides a non-blank value for each of them. If any
     * required value is missing or blank, an {@link IncompleteInstanceException} is
     * thrown listing the offending keys. Otherwise a new instance is built from the
     * request values and saved.
     *
     * @param templateId the identifier of the template the instance is based on
     * @param request    the request holding the values for the template fields
     * @return the newly created and persisted {@link AgentInstance}
     * @throws TemplateNotFoundException   if no template matches templateId
     * @throws IncompleteInstanceException if one or more required field values are missing or blank
     */
    public AgentInstance create(Long templateId, InstanceRequest request) {

        AgentTemplate template = agentTemplateService.getById(templateId);

        Set<String> requiredKeys = new HashSet<>();
        JsonNode fieldsNode = objectMapper.readTree(template.getFields());
        for (JsonNode field : fieldsNode) {
            requiredKeys.add(field.get("key").asText());
        }

        Set<String> missingKeys = new HashSet<>();
        for (String key : requiredKeys) {
            String value = request.values().get(key);
            if (value == null || value.isBlank()) {
                missingKeys.add(key);
            }
        }
        if (!missingKeys.isEmpty()) {
            throw new IncompleteInstanceException(missingKeys);
        }

        AgentInstance instance = new AgentInstance();

        instance.setTemplateId(templateId);
        // TODO(#9): no content check on values — a client could inject prompt-steering
        // text. Guard at assembly/runtime, not at storage.
        instance.setValues(request.values());

        return agentInstanceRepository.save(instance);
    }
}
