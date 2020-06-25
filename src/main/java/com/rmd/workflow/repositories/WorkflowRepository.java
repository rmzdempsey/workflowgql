package com.rmd.workflow.repositories;

import com.rmd.workflow.entities.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkflowRepository extends JpaRepository<Workflow, UUID>{

}
