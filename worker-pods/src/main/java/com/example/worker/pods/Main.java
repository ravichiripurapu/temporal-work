package com.example.worker.pods;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LOGGER.debug("The process starts");
        process();
        LOGGER.debug("The process ends");
        System.exit(0);
    }

    private static void process() {
        try {
            FileUploader fileUploader = new FileUploader("http://minio-service:9000");
            String inputFilePath = System.getenv(Constants.INPUT_FILE_PATH);
            String uploadFilePath = System.getenv(Constants.OUTPUT_FILE_PATH);
            String bucket = System.getenv(Constants.MINIO_BUCKET);
            LOGGER.debug("Environment variable INPUT_FILE_PATH: {}, OUTPUT_FILE_PATH: {}, MINIO_BUCKET: {}", inputFilePath, uploadFilePath, bucket);
            fileUploader.downloadFile(bucket, inputFilePath, Constants.FILE_DOWNLOAD_PATH);
            LOGGER.debug("File downloaded to {}", Constants.FILE_DOWNLOAD_PATH);
            String localOutputFilePath = Worker.transformAndGenerateResult(Constants.FILE_DOWNLOAD_PATH, Constants.FILE_OUTPUT_DIRECTORY);
            LOGGER.debug("File transformed and generated to {}", localOutputFilePath);
            fileUploader.uploadFile(bucket, uploadFilePath, localOutputFilePath);
            LOGGER.debug("File uploaded to {}", uploadFilePath);
        } catch (Exception e) {
            LOGGER.error("Error processing", e);
            throw new RuntimeException("Error processing", e);
        }
    }

}