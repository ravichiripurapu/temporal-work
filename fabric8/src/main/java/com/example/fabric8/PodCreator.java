package com.example.fabric8;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.KubernetesClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.MINUTES;

public class PodCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(PodCreator.class);

    public static void main(String[] args) {
        createFileProcessingPod("files", "input-files/file.txt", "output-files/file.txt");
    }

    public static void createFileProcessingPod(String bucket, String inputFilePath, String outputFilePath) {
        try (KubernetesClient client = new KubernetesClientBuilder().build()) {
            createPod(client, bucket, inputFilePath, outputFilePath);
            listPods(client);

        } catch (KubernetesClientException e) {
            e.printStackTrace();
        }
    }

    private static void createPod(KubernetesClient client, String bucket, String inputFilePath, String outputFilePath) {

        UUID uuid = UUID.randomUUID();
//        String inputFilePath = String.format("input-files/file_%s.txt", uuid);
//        String outputFilePath = String.format("output-files/file_%s.txt", uuid);
        String podName = "docker-file-processor-" + uuid;

//        uploadInputFile(inputFilePath);

        // Define environment variables
        EnvVar minioBucket = new EnvVarBuilder()
                .withName(Constants.MINIO_BUCKET)
                .withValue(bucket)
                .build();

        EnvVar inputFilePathEnvVar = new EnvVarBuilder()
                .withName(Constants.INPUT_FILE_PATH)
                .withValue(inputFilePath)
                .build();

        EnvVar outputFilePathEnvVar = new EnvVarBuilder()
                .withName(Constants.OUTPUT_FILE_PATH)
                .withValue(outputFilePath)
                .build();

        // Define volume mount
        VolumeMount inputDataVolumeMount = new VolumeMountBuilder()
                .withName("input-data-volume")
                .withMountPath("/tmp/docker/input")
                .build();

        VolumeMount outputDataVolumeMount = new VolumeMountBuilder()
                .withName("output-data-volume")
                .withMountPath("/tmp/docker/output")
                .build();

        // Define container
        Container container = new ContainerBuilder()
                .withName("docker-file-processor-container")
                .withImage("flodaio/k8-file-processor:latest")
                .withVolumeMounts(inputDataVolumeMount, outputDataVolumeMount)
                .withEnv(inputFilePathEnvVar, outputFilePathEnvVar, minioBucket)
                .build();

        // Define Volumes
        Volume inputDataVolume = new VolumeBuilder()
                .withName("input-data-volume")
                .withEmptyDir(new EmptyDirVolumeSourceBuilder().build())
                .build();

        Volume outputDataVolume = new VolumeBuilder()
                .withName("output-data-volume")
                .withEmptyDir(new EmptyDirVolumeSourceBuilder().build())
                .build();

        // Define pod spec
        Pod pod = new PodBuilder()
                .withNewMetadata().withName(podName).endMetadata()
                .withNewSpec()
                .withRestartPolicy("Never")
                .withContainers(container)
                .withVolumes(inputDataVolume, outputDataVolume)
                .endSpec()
                .build();

        // Create pod
        client.pods().inNamespace("minio-dev").resource(pod).create();
        LOGGER.debug("Pod created successfully with name: " + podName);

        Pod completedPod = client.pods().inNamespace("minio-dev").withName(podName).waitUntilCondition(
                p -> isPodCompleted(p.getStatus()), 5, MINUTES);

        // Wait until the pod completes
        PodStatus completedStatus = completedPod.getStatus();

        LOGGER.debug("Pod " + podName + " has completed with status: " + completedStatus.getPhase());
    }

    // Custom method to check if the pod has completed
    private static boolean isPodCompleted(PodStatus status) {
        // Check if the pod's phase is Succeeded or Failed
        return "Succeeded".equals(status.getPhase()) || "Failed".equals(status.getPhase());
    }

    private static void uploadInputFile(String inputFilePath) {
        LOGGER.debug("Uploading input file to Minio to path: {}", inputFilePath);
        FileUploader fileUploader = new FileUploader("http://127.0.0.1:9000");
        try {
            fileUploader.uploadFile("files", inputFilePath, "D:\\Personal\\workspace\\docker-file-processor\\src\\test\\resources\\file.txt");
            LOGGER.debug("File uploaded successfully.");
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static void listPods(KubernetesClient client) {
        client.pods().inNamespace("default").list().getItems().forEach(pod -> {
            System.out.println("Name: " + pod.getMetadata().getName());
        });
    }
}
