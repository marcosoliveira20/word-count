package br.com.marcosoliveira20.filemanager.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "processed_line",
        uniqueConstraints = @UniqueConstraint(name="uk_user_word_source_line", columnNames = {"user_id","word","source_file","line_number"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProcessedLine {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable=false, length=64)
    private String userId;

    @Column(name="word", nullable=false, length=200)
    private String word;

    @Column(name="source_file", nullable=false, length=512)
    private String sourceFile;

    @Column(name="line_number", nullable=false)
    private Long lineNumber;

    @Column(name="line_hash", nullable=false, length=40)
    private String lineHash;

    @Column(name="created_at", nullable=false)
    private Instant createdAt;
}
