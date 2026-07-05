package com.st00mp.agentindexbackend.service;

import com.st00mp.agentindexbackend.entity.AgentInstance;
import com.st00mp.agentindexbackend.entity.AgentTemplate;
import com.st00mp.agentindexbackend.exception.InstanceNotFoundException;
import com.st00mp.agentindexbackend.exception.UnresolvedPlaceholderException;
import com.st00mp.agentindexbackend.repository.AgentInstanceRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Assembles instruction strings by resolving {@code {{placeholder}}} tokens against a value map.
 */
@Service
public class AssemblyService {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{(\\w+)\\}\\}");
    private final AgentInstanceRepository agentInstanceRepository;
    private final AgentTemplateService agentTemplateService;

    public AssemblyService(AgentInstanceRepository agentInstanceRepository, AgentTemplateService agentTemplateService) {
        this.agentInstanceRepository = agentInstanceRepository;
        this.agentTemplateService = agentTemplateService;
    }

    /**
     * Builds the final output for an agent instance by assembling its template
     * instructions with the instance's placeholder values.
     *
     * @param instanceId id of the agent instance
     * @return the assembled instructions
     * @throws InstanceNotFoundException      if no instance exists with the given id
     * @throws UnresolvedPlaceholderException if any placeholder has no corresponding value
     */
    public String assembleOutput(Long instanceId) {

        AgentInstance instance = agentInstanceRepository.findById(instanceId)
                .orElseThrow(() -> new InstanceNotFoundException(instanceId));

        AgentTemplate template = agentTemplateService.getById(instance.getTemplateId());

        return assemble(template.getInstructions(), instance.getValues());
    }

    /**
     * Resolves all placeholders in {@code instructions} using the provided {@code values}.
     *
     * @param instructions template string containing {@code {{key}}} placeholders
     * @param values       map of keys to their replacement values
     * @return the assembled string with all placeholders replaced
     * @throws UnresolvedPlaceholderException if any placeholder has no corresponding value
     */
    public String assemble(String instructions, Map<String, String> values) {

        Set<String> missing = new HashSet<>();
        Matcher matcher = PLACEHOLDER.matcher(instructions);

        while (matcher.find()) {
            String key = matcher.group(1);
            String value = values.get(key);
            if (value == null || value.isBlank()) {
                missing.add(key);
            }
        }

        if (!missing.isEmpty()) {
            throw new UnresolvedPlaceholderException(missing);
        }

        return PLACEHOLDER.matcher(instructions)
                .replaceAll(match -> values.get(match.group(1)));
    }
}