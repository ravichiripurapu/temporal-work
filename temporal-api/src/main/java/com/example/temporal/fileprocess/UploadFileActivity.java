package com.example.temporal.fileprocess;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface UploadFileActivity {
    @ActivityMethod
    void uploadFile(String localFilePath, String bucket, String minioFilePath);

    @ActivityMethod
    String readContent(String bucket, String outputFilePath);
}
