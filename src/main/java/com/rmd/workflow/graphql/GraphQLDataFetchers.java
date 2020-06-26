package com.rmd.workflow.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rmd.workflow.entities.Workflow;
import com.rmd.workflow.entities.WorkflowVersion;
import com.rmd.workflow.graphql.inputs.CreateWorkflowRequest;
import com.rmd.workflow.services.WorkflowService;
import graphql.schema.DataFetcher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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
            CreateWorkflowRequest input = objectMapper.convertValue(dataFetchingEnvironment.getArgument("request"), CreateWorkflowRequest.class);;
            return workflowService.createWorkflow(input);
        };
    }

    public DataFetcher<List<WorkflowVersion>> getVersionsDataFetcher(){
        return dataFetchingEnvironment -> {
            Workflow workflow = dataFetchingEnvironment.getSource();
            UUID workflowId = workflow.getUuid();
            return this.workflowService.getVersionsByWorkflowUuid(workflowId);
        };
    }

    public DataFetcher<LocalDateTime> getCreatedOnDataFetcher(){
        return dataFetchingEnvironment -> {
            WorkflowVersion version = dataFetchingEnvironment.getSource();
            return version.getCreatedOn();
        };
    }

}
