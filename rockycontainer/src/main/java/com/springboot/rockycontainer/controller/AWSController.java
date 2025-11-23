package com.springboot.rockycontainer.controller;

import com.springboot.rockycontainer.service.AWSS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/aws/")
@RequiredArgsConstructor
public class AWSController {

    private final AWSS3Service awss3Service;

    @GetMapping("v1/health")
    public String testEndpoint() {
        return "testing from container";
    }

    @PostMapping("v1/S3/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam("bucketName") String bucketName) {
        try {
            String url = awss3Service.uploadFile(file,bucketName);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Upload failed: " + e.getMessage());
        }

    }

    @GetMapping("v1/s3/download/{bucketname}/{filename}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String bucketname,
                                               @PathVariable String filename) {
        try {
            String key = "uploads/" + filename;
            byte[] fileBytes = awss3Service.downloadFile(key,bucketname);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + filename)
                    .body(fileBytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(("Download failed: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("v1/S3/presign-upload")
    public Map<String, String> getPresignedUploadUrl(
            @RequestParam String bucketName,
            @RequestParam String key
    ) {
        String url = awss3Service.presignUploadUrl(key, bucketName);
        return Map.of(
                "bucket", bucketName,
                "key", key,
                "uploadUrl", url
        );
    }

    @GetMapping("v1/S3/presign-download")
    public Map<String, String> getPresignedDownloadUrl(
            @RequestParam String bucketName,
            @RequestParam String key
    ) {
        String url = awss3Service.presignDownloadUrl(key, bucketName);
        return Map.of(
                "bucket", bucketName,
                "key", key,
                "downloadUrl", url
        );
    }



}
