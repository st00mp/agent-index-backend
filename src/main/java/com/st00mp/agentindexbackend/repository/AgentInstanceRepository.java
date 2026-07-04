package com.st00mp.agentindexbackend.repository;

import com.st00mp.agentindexbackend.entity.AgentInstance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentInstanceRepository extends JpaRepository<AgentInstance, Long> {}
