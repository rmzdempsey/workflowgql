package com.rmd.workflow.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
public class WorkflowVersion {
    @Id
    @GeneratedValue
    private UUID uuid;

    private int versionNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflowUuid")
    private Workflow workflow;
}
