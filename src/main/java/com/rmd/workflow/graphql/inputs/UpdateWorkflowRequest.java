package com.rmd.workflow.graphql.inputs;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateWorkflowRequest {
    private UUID uuid;
    private String name;
    private String description;
    private String comment;
    private String workflowJson;
}
