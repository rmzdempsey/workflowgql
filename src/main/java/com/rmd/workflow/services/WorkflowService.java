package com.rmd.workflow.services;

import com.rmd.workflow.entities.Workflow;
import com.rmd.workflow.entities.WorkflowVersion;
import com.rmd.workflow.repositories.WorkflowRepository;
import com.rmd.workflow.repositories.WorkflowVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkflowService {

    final WorkflowRepository workflowRepository;
    final WorkflowVersionRepository workflowVersionRepository;

    public List<Workflow> getAllWorkflows(){
        return this.workflowRepository.findAll();
    }

    public Workflow createWorkflow(Workflow workflow ){

        WorkflowVersion wfv = new WorkflowVersion();
        wfv.setVersionNo(1);
        wfv.setWorkflow(workflow);

        workflow.getVersions().add(wfv);

        return this.workflowRepository.save(workflow);

    }

    public List<WorkflowVersion> getVersionsByWorkflowUuid(UUID uuid){
        return this.workflowVersionRepository.findByWorkflowUuid(uuid);
    }
}
