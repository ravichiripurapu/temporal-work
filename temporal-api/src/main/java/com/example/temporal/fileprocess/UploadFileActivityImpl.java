package com.example.temporal.fileprocess;

import com.example.temporal.minio.FileUploader;
import com.example.temporal.shared.Shared;
import io.temporal.spring.boot.ActivityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component
@ActivityImpl(taskQueues = Shared.FILE_PROCESSING_TASK_QUEUE)
public class UploadFileActivityImpl implements UploadFileActivity {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadFileActivityImpl.class);

    @Override
    public void uploadFile(String localFilePath, String bucket, String minioInputFilePath) {
        LOGGER.debug("Uploading input file to Minio to path: {}", minioInputFilePath);

        FileUploader fileUploader = new FileUploader("http://127.0.0.1:9000");
        try {
            fileUploader.uploadFile(bucket, minioInputFilePath, localFilePath);
            LOGGER.debug("File uploaded successfully.");
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String readContent(String bucket, String outputFilePath) {
        FileUploader fileUploader = new FileUploader("http://127.0.0.1:9000");
        try {
            return fileUploader.getFileContent(bucket, outputFilePath);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


}
