package br.com.marcosoliveira20.filemanager.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "processed_object",
        uniqueConstraints = @UniqueConstraint(name = "uk_object_key_etag", columnNames = {"object_key","etag"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProcessedObject {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="object_key", nullable=false, length=512)
    private String objectKey;

    @Column(name="etag", nullable=false, length=64)
    private String etag;

    @Column(name="status", nullable=false, length=20)
    private String status; // SUCCESS | FAILED | PARTIAL

    @Column(name="message", length=1000)
    private String message;

    @Column(name="lines_total")
    private Integer linesTotal;

    @Column(name="processed_at", nullable=false)
    private Instant processedAt;
}
