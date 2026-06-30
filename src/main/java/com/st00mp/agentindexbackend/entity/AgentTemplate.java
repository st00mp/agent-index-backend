package com.st00mp.agentindexbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class AgentTemplate {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Lob
    @Column(nullable = false)
    private String fields;

    @Column(nullable = false)
    private String version;
}
