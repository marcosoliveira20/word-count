package br.com.marcosoliveira20.english.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "oxford_level",
        indexes = @Index(name = "uk_oxford_level_name", columnList = "name", unique = true))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OxfordLevel {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Exemplos de valores: A1, A2, B1, B2, C1, C2
    @Column(name = "name", length = 50, nullable = false, unique = true)
    private String name;
}
