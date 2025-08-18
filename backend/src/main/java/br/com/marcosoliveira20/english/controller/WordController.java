package br.com.marcosoliveira20.english.controller;

import br.com.marcosoliveira20.english.dto.LevelCountDTO;
import br.com.marcosoliveira20.english.dto.NumberDTO;
import br.com.marcosoliveira20.english.dto.UsageRequestDTO;
import br.com.marcosoliveira20.english.dto.WordUsageDTO;
import br.com.marcosoliveira20.english.model.AppUser;
import br.com.marcosoliveira20.english.model.WordUsageLog;
import br.com.marcosoliveira20.english.service.AppUserService;
import br.com.marcosoliveira20.english.service.WordService;
import br.com.marcosoliveira20.english.service.WordUsageLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/words", produces = MediaType.APPLICATION_JSON_VALUE)
@PreAuthorize("hasAnyAuthority('ROLE_WRITER','ROLE_writer')")
@RequiredArgsConstructor
public class WordController {

    private final WordService wordService;
    private final WordUsageLogService wordUsageLogService;
    private final AppUserService appUserService;


    /**
     * Registra uso por NOME. Cria a Word global se não existir e registra uso para o usuário.
     * Body: { "name": "get" }
     * 201 Created + Location: /api/words/{wordId}/usage
     */
    @PostMapping(path = "/usage", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUsageByName(@RequestBody UsageRequestDTO body,
                                                 @AuthenticationPrincipal Jwt jwt) {
        if (body == null || body.getName() == null || body.getName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "name é obrigatório"));
        }

        AppUser user = appUserService.resolveFromJwt(jwt);
        WordUsageLog saved = wordService.registerUsageByName(body.getName().trim(), user);

        return ResponseEntity.ok(saved);
    }

    @GetMapping(path = "/info")
    public ResponseEntity<?> getCountWord( @AuthenticationPrincipal Jwt jwt) {

        NumberDTO dto = new NumberDTO();

        AppUser user = appUserService.resolveFromJwt(jwt);
        dto.setNumber(wordUsageLogService.countUsageByUser(user));

        return ResponseEntity.ok(dto);
    }

    /**
     * Dashboard: retorna contagem por nível (palavras DISTINTAS usadas pelo usuário).
     * Response:
     * {
     *   "levels": [ { "level": "A1", "count": 20 }, ... ]
     * }
     */
    @GetMapping("/levels")
    public ResponseEntity<?> levels(@AuthenticationPrincipal Jwt jwt) {
        AppUser user = appUserService.resolveFromJwt(jwt);
        List<LevelCountDTO> rows =
                wordService.countDistinctWordsByLevelForUser(user.getId());
        return ResponseEntity.ok(Map.of("levels", rows));
    }

    @GetMapping
    public ResponseEntity<?> listByLevel(
            @RequestParam(name = "level", required = false) String level,
            @AuthenticationPrincipal Jwt jwt) {

        if (level == null || level.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Parâmetro level é obrigatório"));
        }

        String userId = jwt.getSubject();
        List<WordUsageDTO> words = wordService.findByLevelAndUser(level, userId);

        return ResponseEntity.ok(Map.of("words", words));
    }
}
