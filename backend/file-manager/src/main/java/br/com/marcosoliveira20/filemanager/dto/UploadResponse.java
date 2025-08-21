package br.com.marcosoliveira20.filemanager.dto;// src/main/java/.../api/dto/UploadResponse.java

import java.time.Instant;

public record UploadResponse(
        String bucket,
        String objectKey,
        long size,
        String contentType,
        String status,
        Instant storedAt
) {}
