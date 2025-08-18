package br.com.marcosoliveira20.english.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "word_usage_log",
        indexes = {
                @Index(name = "idx_wul_user", columnList = "user_id"),
                @Index(name = "idx_wul_word", columnList = "word_id"),
                @Index(name = "idx_wul_user_word", columnList = "user_id,word_id"),
                @Index(name = "idx_wul_used_at", columnList = "used_at")
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "id")
public class WordUsageLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // palavra usada (global)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    // quem usou
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "used_at", nullable = false)
    private LocalDateTime usedAt;
}
