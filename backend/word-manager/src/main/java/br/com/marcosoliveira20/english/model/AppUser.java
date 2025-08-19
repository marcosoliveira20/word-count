package br.com.marcosoliveira20.english.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "app_user")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AppUser {
    @Id
    @Column(length = 50)
    private String id; // sub do Keycloak

    @Column(nullable = false, length = 100)
    private String username;
}
