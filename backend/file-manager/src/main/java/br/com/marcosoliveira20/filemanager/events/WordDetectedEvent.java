package br.com.marcosoliveira20.filemanager.events;

public record WordDetectedEvent(
        String word,
        String userId
) {}