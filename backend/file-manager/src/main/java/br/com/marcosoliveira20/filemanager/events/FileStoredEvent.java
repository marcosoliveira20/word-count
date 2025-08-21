package br.com.marcosoliveira20.filemanager.events;

public record FileStoredEvent(
        String bucket,
        String objectKey,
        String userId,
        String contentType
) {}
