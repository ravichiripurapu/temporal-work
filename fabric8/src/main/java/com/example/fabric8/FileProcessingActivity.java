package com.example.fabric8;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface FileProcessingActivity {

    @ActivityMethod
    void processFile(String bucket, String inputFilePath, String outputFilePath);

}
