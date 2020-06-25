package com.rmd.workflow.repositories;

import com.rmd.workflow.entities.Workflow;
import com.rmd.workflow.entities.WorkflowVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WorkflowVersionRepository extends JpaRepository<WorkflowVersion, UUID>{

    List<WorkflowVersion> findByWorkflowUuid(UUID uuid);
}
