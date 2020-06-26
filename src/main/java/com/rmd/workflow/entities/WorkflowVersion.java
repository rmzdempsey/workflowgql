package com.rmd.workflow.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.Instant;
import java.time.LocalDateTime;
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

    private String name;
    private String description;
    private String editComment;
    private UUID parentVersionUuid;
    private String createdBy;
    private LocalDateTime createdOn;
    private String workflowJson;
}
