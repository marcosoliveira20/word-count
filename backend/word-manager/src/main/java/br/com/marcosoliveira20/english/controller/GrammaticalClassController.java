package br.com.marcosoliveira20.english.controller;

import br.com.marcosoliveira20.english.model.GrammaticalClass;
import br.com.marcosoliveira20.english.service.GrammaticalClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grammatical-classes")
@PreAuthorize("hasAnyAuthority('ROLE_WRITER','ROLE_writer')")
@RequiredArgsConstructor
public class GrammaticalClassController {

    private final GrammaticalClassService grammaticalClassService;

    @GetMapping
    public ResponseEntity<List<GrammaticalClass>> list() {
        return ResponseEntity.ok(grammaticalClassService.listAll());
    }
}
