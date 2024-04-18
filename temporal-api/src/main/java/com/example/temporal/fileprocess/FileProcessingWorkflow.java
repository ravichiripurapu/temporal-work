package com.example.temporal.fileprocess;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface FileProcessingWorkflow {
    @WorkflowMethod
    String processFile(String localFilePath);
}
