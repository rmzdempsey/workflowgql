package com.rmd.workflow.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class Workflow {
    @Id
    @GeneratedValue
    private UUID uuid;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "workflow",cascade = CascadeType.ALL)
    private List<WorkflowVersion> versions;

    private int releasedVersionNo;
}
