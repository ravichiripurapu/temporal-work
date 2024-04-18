package com.example.temporal.controller;

import com.example.temporal.fileprocess.FileProcessingWorkflow;
import com.example.temporal.shared.Shared;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FileProcessingController {
    private final WorkflowClient client;

    @GetMapping(value = "/file/process")
    private ResponseEntity<String> fileProcess() {

        FileProcessingWorkflow workflow =
                client.newWorkflowStub(
                        FileProcessingWorkflow.class,
                        WorkflowOptions.newBuilder()
                                .setTaskQueue(Shared.FILE_PROCESSING_TASK_QUEUE)
                                .setWorkflowId("file-process-workflow-id")
                                .build());

        String transformedContent = workflow.processFile("D:\\Personal\\workspace\\docker-file-processor\\src\\test\\resources\\file.txt");

        String workflowId = WorkflowStub.fromTyped(workflow).getExecution().getWorkflowId();
        // Display workflow execution results
        log.debug("Successfully executed workflow with id: {} and result: {}", workflowId, transformedContent);

        // bypass thymeleaf, don't return template name just result
        return new ResponseEntity<>(transformedContent, HttpStatus.OK);
    }
}
