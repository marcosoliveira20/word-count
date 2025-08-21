package br.com.marcosoliveira20.filemanager.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MinioEvent(
        @JsonProperty("EventName") String eventName,
        @JsonProperty("Records") List<Record> records,
        String userId,               // opcional – pode vir na sua key ou setado pelo presign
        String correlationId
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Record(S3 s3) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record S3(Bucket bucket, Obj object) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Bucket(String name) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Obj(
            @JsonProperty("key") String key,
            @JsonProperty("eTag") String eTag,
            @JsonProperty("contentType") String contentType,
            @JsonProperty("size") Long size
    ) {}
}
