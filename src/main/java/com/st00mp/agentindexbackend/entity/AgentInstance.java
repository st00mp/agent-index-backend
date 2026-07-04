package com.st00mp.agentindexbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@Entity
public class AgentInstance {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long templateId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "field_values", nullable = false)    // "values" is a reserved SQL keyword
    private Map<String,String> values;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

}
