package br.com.marcosoliveira20.english.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "grammatical_class",
        uniqueConstraints = @UniqueConstraint(name = "uk_grammatical_class_name", columnNames = "name")
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "id")
public class GrammaticalClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // noun, verb, adjective, adverb, ...
    @Column(nullable = false, length = 50)
    private String name;
}
