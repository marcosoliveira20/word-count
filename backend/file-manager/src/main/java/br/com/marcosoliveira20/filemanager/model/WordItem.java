package br.com.marcosoliveira20.filemanager.model;

import java.time.Instant;
import java.util.List;

public record WordItem(
        String word,
        String oxfordLevel,
        List<String> grammaticalClasses,
        String userId,
        Instant detectedAt,
        String sourceFile,
        long lineNumber,
        String lineHash
) {}
