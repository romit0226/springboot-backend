package com.springboot.rockycontainer.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AWSS3Service {

    private final AmazonS3 amazonS3;

    public String uploadFile(MultipartFile file, String bucketName){

        try {
            String key = "uploads/" + file.getOriginalFilename();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            amazonS3.putObject(bucketName, key, file.getInputStream(), metadata);

            return amazonS3.getUrl(bucketName, key).toString();
        } catch (Exception ex) {
            // temp throw statment
            throw new RuntimeException(ex);
        }

    }

    public byte[] downloadFile(String key,String bucketName) {
        try {
            S3Object s3object = amazonS3.getObject(bucketName, key);
            S3ObjectInputStream inputStream = s3object.getObjectContent();

            return IOUtils.toByteArray(inputStream);

        } catch (Exception e) {
            throw new RuntimeException("Failed to download file: " + key, e);
        }
    }


}
