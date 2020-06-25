package com.rmd.workflow.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rmd.workflow.entities.Workflow;
import com.rmd.workflow.entities.WorkflowVersion;
import com.rmd.workflow.graphql.inputs.WorkflowInput;
import com.rmd.workflow.services.WorkflowService;
import graphql.schema.DataFetcher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class GraphQLDataFetchers {

    private final WorkflowService workflowService;
    private final ObjectMapper objectMapper;

    public DataFetcher allWorkflowsDataFetcher() {
        return dataFetchingEnvironment -> {
            return workflowService.getAllWorkflows();
        };
    }

    public DataFetcher createWorkflowDataFetcher(){
        return dataFetchingEnvironment -> {
            WorkflowInput input = objectMapper.convertValue(dataFetchingEnvironment.getArgument("workflow"), WorkflowInput.class);;
            Workflow workflow = new Workflow();
            workflow.setName(input.getName());
            workflow.setDescription(input.getDescription());
            workflow.setVersions(new ArrayList<>());
            return workflowService.createWorkflow(workflow);
        };
    }

    public DataFetcher<List<WorkflowVersion>> getVersionsDataFetcher(){
        return dataFetchingEnvironment -> {
            Workflow workflow = dataFetchingEnvironment.getSource();
            UUID workflowId = workflow.getUuid();
            return this.workflowService.getVersionsByWorkflowUuid(workflowId);
        };
    }

}
