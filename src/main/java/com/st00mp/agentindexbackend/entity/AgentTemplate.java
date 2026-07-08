package com.st00mp.agentindexbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Getter
@Setter
@Entity
public class AgentTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(length = 1000, nullable = false)
    private String description;

    @Lob
    @Column(nullable = false)
    private String instructions;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    private List<FieldDefinition> fields;

    @Column(nullable = false)
    private String version;
}
