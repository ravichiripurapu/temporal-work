package com.example.fabric8;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileProcessingActivityImpl implements FileProcessingActivity {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileProcessingActivityImpl.class);


    @Override
    public void processFile(String bucket, String inputFilePath, String outputFilePath) {
        PodCreator.createFileProcessingPod(bucket, inputFilePath, outputFilePath);
    }

}
