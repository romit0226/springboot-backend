package com.springboot.rockycontainer.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AWSS3Service {

    private final S3Client s3Client;
    private final S3Presigner presigner;

    public String uploadFile(MultipartFile file, String bucketName){

        try {
            String key = "uploads/" + file.getOriginalFilename();

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            // FIX: use file.getBytes() to avoid "mark/reset" errors
            s3Client.putObject(
                    putRequest,
                    RequestBody.fromBytes(file.getBytes())
            );

            return getObjectUrl(bucketName, key);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to upload file", ex);
        }

    }

    public byte[] downloadFile(String key, String bucketName) {

        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            ResponseBytes<GetObjectResponse> response =
                    s3Client.getObjectAsBytes(getObjectRequest);

            return response.asByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to download file: " + key, e);
        }
    }

    public String presignUploadUrl(String key, String bucketName) {

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("application/octet-stream")
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedObject = presigner.presignPutObject(presignRequest);

        return presignedObject.url().toString();
    }

    public String presignDownloadUrl(String key, String bucketName) {

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedObject = presigner.presignGetObject(presignRequest);

        return presignedObject.url().toString();
    }

    private String getObjectUrl(String bucket, String key) {
        return "https://" + bucket + ".s3.amazonaws.com/" + key;
    }


}
