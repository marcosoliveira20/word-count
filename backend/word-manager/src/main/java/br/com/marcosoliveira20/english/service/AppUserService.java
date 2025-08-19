package br.com.marcosoliveira20.english.service;

import br.com.marcosoliveira20.english.model.AppUser;
import br.com.marcosoliveira20.english.repository.AppUserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepo appUserRepo;

    /**
     * Resolve (ou cria) o AppUser a partir do JWT.
     * id = sub (Keycloak). username = preferred_username (fallback para sub).
     */
    @Transactional
    public AppUser resolveFromJwt(Jwt jwt) {
        final String sub = jwt.getSubject();
        final String preferredUsername = Optional.ofNullable(jwt.getClaimAsString("preferred_username"))
                .filter(s -> !s.isBlank())
                .orElse(sub);

        return appUserRepo.findById(sub).orElseGet(() ->
                appUserRepo.save(AppUser.builder()
                        .id(sub)
                        .username(preferredUsername)
                        .build())
        );
    }
}
