package com.example.temporal.fileprocess;

import com.example.temporal.shared.Shared;
import io.temporal.activity.ActivityOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Component
@WorkflowImpl(taskQueues = Shared.FILE_PROCESSING_TASK_QUEUE)
public class FileProcessingWorkflowImpl implements FileProcessingWorkflow {

    ActivityOptions options = ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(60))
            .build();
    @Override
    public String processFile(String localFilePath) {
        UUID uuid = UUID.randomUUID();
        String inputFilePath = String.format("input-files/file_%s.txt", uuid);
        String outputFilePath = String.format("output-files/file_%s.txt", uuid);
        FileProcessingActivity fileProcessingActivity = Workflow.newActivityStub(FileProcessingActivity.class, options);
        UploadFileActivity uploadFileActivity = Workflow.newActivityStub(UploadFileActivity.class, options);
        uploadFileActivity.uploadFile(localFilePath, "files", inputFilePath);
        fileProcessingActivity.processFile("files", inputFilePath, outputFilePath);
        return uploadFileActivity.readContent("files", outputFilePath);
    }
}
