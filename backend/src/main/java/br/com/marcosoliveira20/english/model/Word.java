package br.com.marcosoliveira20.english.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "word",
        indexes = {
                @Index(name = "idx_word_name", columnList = "name"),
                @Index(name = "idx_word_created_at", columnList = "created_at"),
                @Index(name = "idx_word_updated_at", columnList = "updated_at")
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Word {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 255, nullable = false, unique = true)
    private String name;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // N:N com oxford_level via word_oxford_level
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "word_oxford_level",
            joinColumns = @JoinColumn(name = "word_id"),
            inverseJoinColumns = @JoinColumn(name = "oxford_level_id")
    )
    @Builder.Default
    private Set<OxfordLevel> oxfordLevels = new HashSet<>();

    // N:N com grammatical_class via word_grammatical_class
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "word_grammatical_class",
            joinColumns = @JoinColumn(name = "word_id"),
            inverseJoinColumns = @JoinColumn(name = "grammatical_class_id")
    )
    @Builder.Default
    private Set<GrammaticalClass> grammaticalClasses = new HashSet<>();
}
