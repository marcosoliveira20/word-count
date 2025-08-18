package br.com.marcosoliveira20.english.controller;

import br.com.marcosoliveira20.english.model.OxfordLevel;
import br.com.marcosoliveira20.english.service.OxfordLevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/levels")
@PreAuthorize("hasAnyAuthority('ROLE_WRITER','ROLE_writer')")
@RequiredArgsConstructor
public class OxfordLevelController {

    private final OxfordLevelService oxfordLevelService;

    @GetMapping
    public ResponseEntity<List<OxfordLevel>> list() {
        return ResponseEntity.ok(oxfordLevelService.listAll());
    }
}
