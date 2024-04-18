package com.example.temporal.fileprocess;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface FileProcessingActivity {
    @ActivityMethod
    void processFile(String bucket, String inputFilePath, String outputFilePath);
}
