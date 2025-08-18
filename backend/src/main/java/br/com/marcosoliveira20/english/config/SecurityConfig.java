package br.com.marcosoliveira20.english.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.security.web.SecurityFilterChain;

import java.util.*;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(auth -> auth
                // Catálogos públicos
                .requestMatchers(HttpMethod.GET, "/api/levels").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/grammatical-classes").permitAll()

                // Lista palavras e busca - pode ser público ou autenticado, ajuste conforme necessário
                .requestMatchers(HttpMethod.GET, "/api/words").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/words/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/words/levels").permitAll()

                // Criação de palavra (precisa de role)
                .requestMatchers(HttpMethod.POST, "/api/words")
                .hasAnyAuthority("ROLE_WRITER", "ROLE_writer")

                // Registro de uso de palavra (precisa de role)
                .requestMatchers(HttpMethod.POST, "/api/words/*/usage")
                .hasAnyAuthority("ROLE_WRITER", "ROLE_writer")

                // Qualquer outra requisição exige autenticação
                .anyRequest().authenticated()
        );


        http.oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter())));

        http.cors(Customizer.withDefaults());
        return http.build();
    }

    private JwtAuthenticationConverter jwtAuthConverter() {
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(this::extractRealmRoles);
        return converter;
    }

    private Collection<org.springframework.security.core.GrantedAuthority> extractRealmRoles(Jwt jwt) {
        var mapper = new SimpleAuthorityMapper();
        mapper.setConvertToUpperCase(true);
        mapper.setPrefix("ROLE_");

        Set<String> roles = new HashSet<>();
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null) {
            Object rolesObj = realmAccess.get("roles");
            if (rolesObj instanceof Collection<?> col) {
                col.forEach(r -> roles.add(String.valueOf(r)));
            }
        }
        return mapper.mapAuthorities(
                roles.stream().map(SimpleGrantedAuthority::new).toList()
        );
    }

    @Bean
    JwtDecoder jwtDecoder() {
        String jwkSetUri = "http://keycloak:8080/realms/english-realm/protocol/openid-connect/certs";
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer("http://localhost:8081/realms/english-realm"));
        return decoder;
    }

}
