package com.rmd.workflow.services;

import com.rmd.workflow.entities.Workflow;
import com.rmd.workflow.entities.WorkflowVersion;
import com.rmd.workflow.graphql.inputs.CreateWorkflowRequest;
import com.rmd.workflow.graphql.inputs.UpdateWorkflowRequest;
import com.rmd.workflow.repositories.WorkflowRepository;
import com.rmd.workflow.repositories.WorkflowVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkflowService {

    final WorkflowRepository workflowRepository;
    final WorkflowVersionRepository workflowVersionRepository;

    public List<Workflow> getAllWorkflows(){
        return this.workflowRepository.findAll();
    }

    public Workflow createWorkflow(CreateWorkflowRequest request ){

        Workflow workflow = new Workflow();

        workflow.setVersions(new ArrayList<>());

        WorkflowVersion wfv = new WorkflowVersion();
        wfv.setVersionNo(1);
        wfv.setWorkflow(workflow);
        wfv.setName(request.getName());
        wfv.setDescription(request.getDescription());
        wfv.setEditComment("Created");
        wfv.setCreatedBy("rmd");
        wfv.setWorkflowJson(request.getWorkflowJson());
        wfv.setCreatedOn(LocalDateTime.now());

        workflow.getVersions().add(wfv);
        workflow.setReleasedVersionNo(1);

        return this.workflowRepository.save(workflow);

    }

    public Workflow updateWorkflow(UpdateWorkflowRequest request ){

        Optional<Workflow> wf = this.workflowRepository.findById(request.getWorkflowUuid());
        if( wf.isPresent() ){

            Workflow workflow = wf.get();

            WorkflowVersion wfv = new WorkflowVersion();

            wfv.setParentVersionNo(request.getVersionNo());
            wfv.setVersionNo(this.workflowVersionRepository.findByWorkflowUuid(request.getWorkflowUuid()).size()+1);
            wfv.setWorkflow(workflow);
            wfv.setName(request.getName());
            wfv.setDescription(request.getDescription());
            wfv.setEditComment(request.getComment());
            wfv.setCreatedBy("rmd");
            wfv.setWorkflowJson(request.getWorkflowJson());
            wfv.setCreatedOn(LocalDateTime.now());

            workflow.getVersions().add(wfv);

            return this.workflowRepository.save(workflow);
        }

        return null;
    }

    public List<WorkflowVersion> getVersionsByWorkflowUuid(UUID uuid){
        return this.workflowVersionRepository.findByWorkflowUuid(uuid);
    }
}
