package com.rmd.workflow.graphql.inputs;

import lombok.Data;

@Data
public class CreateWorkflowRequest {
    private String name;
    private String description;
    private String workflowJson;
}
