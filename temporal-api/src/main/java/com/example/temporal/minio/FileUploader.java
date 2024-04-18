package com.example.temporal.minio;

import io.minio.*;
import io.minio.errors.MinioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class FileUploader {
  private static final Logger LOGGER = LoggerFactory.getLogger(FileUploader.class);

  private final MinioClient minioClient;

  public FileUploader(String endpoint) {
    this.minioClient = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials("minioadmin", "minioadmin")
                    .build();
  }

  public void uploadFile(String bucket, String object, String filepath) throws InvalidKeyException, IOException, NoSuchAlgorithmException {
    try {


      // Make 'input-files' bucket if not exist.
      boolean found =
              minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
      if (!found) {
        // Make a new bucket called 'input-files'.
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
      } else {
        LOGGER.debug("Bucket 'input-files' already exists.");
      }

      // Upload '/home/user/Photos/asiaphotos.zip' as object name 'asiaphotos-2015.zip' to bucket
      // 'asiatrip'.
      minioClient.uploadObject(
              UploadObjectArgs.builder()
                      .bucket(bucket)
                      .object(object)
                      .filename(filepath)
                      .build());
      LOGGER.debug(
              "file is successfully uploaded as "
                      + "object " + object + " to bucket " + bucket);


    } catch (MinioException e) {
      LOGGER.debug("Error occurred: " + e);
      LOGGER.debug("HTTP trace: " + e.httpTrace());
    }
  }

  // get file content
    public String getFileContent(String bucket, String object) throws InvalidKeyException, IOException, NoSuchAlgorithmException {
        try (InputStream stream =
                     minioClient.getObject(GetObjectArgs
                             .builder()
                             .bucket(bucket)
                             .object(object)
                             .build())) {
            // Read the stream and assign the data to a string variable
            String content = new String(stream.readAllBytes());
            LOGGER.debug("Object " + object + " is downloaded.");
            return content;
        } catch (MinioException e) {
            LOGGER.debug("Error occurred: " + e);
            LOGGER.debug("HTTP trace: " + e.httpTrace());
            throw new RuntimeException(e);
        }
    }

  // download file
    public void downloadFile(String bucket, String object, String filepath) throws InvalidKeyException, IOException, NoSuchAlgorithmException {
        try {
        minioClient.downloadObject(
                DownloadObjectArgs.builder()
                        .bucket(bucket)
                        .object(object)
                        .filename(filepath)
                        .build());
        LOGGER.debug("Object " + object + " is downloaded to " + filepath + ".");
        } catch (MinioException e) {
          LOGGER.debug("Error occurred: " + e);
          LOGGER.debug("HTTP trace: " + e.httpTrace());
        }
    }

    // delete file
    public void deleteFile(String bucket, String object) throws InvalidKeyException, IOException, NoSuchAlgorithmException {
        try {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucket)
                        .object(object)
                        .build());
        LOGGER.debug(
                "Object " + object + " is deleted.");
        } catch (MinioException e) {
          LOGGER.debug("Error occurred: " + e);
          LOGGER.debug("HTTP trace: " + e.httpTrace());
        }
    }


}