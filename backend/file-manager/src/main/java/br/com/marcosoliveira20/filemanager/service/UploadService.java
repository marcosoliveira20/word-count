// src/main/java/.../service/UploadService.java
package br.com.marcosoliveira20.filemanager.service;
import io.minio.*;
import io.minio.errors.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadService {

    private final MinioClient minioClient;

    @Getter
    @Value("${minio.bucket}")
    private String bucket;

    @Getter
    @Value("${topics.fileStored}")
    private String fileStoredTopic;

    public String store(MultipartFile file, String userId) throws Exception {
        String objectKey = "uploads/%s/%s-%s.txt".formatted(
                userId,
                System.currentTimeMillis(),
                UUID.randomUUID()
        );

        // garante bucket
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!exists) minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());

        // upload
        try (var in = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .contentType("text/plain")
                            .stream(in, file.getSize(), -1)
                            .build()
            );
        }
        return objectKey;
    }
}