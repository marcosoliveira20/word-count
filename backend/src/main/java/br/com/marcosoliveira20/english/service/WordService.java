package br.com.marcosoliveira20.english.service;

import br.com.marcosoliveira20.english.dto.LevelCountDTO;
import br.com.marcosoliveira20.english.dto.WordDetailsDTO;
import br.com.marcosoliveira20.english.dto.WordUsageDTO;
import br.com.marcosoliveira20.english.model.AppUser;
import br.com.marcosoliveira20.english.model.Word;
import br.com.marcosoliveira20.english.model.WordUsageLog;
import br.com.marcosoliveira20.english.repository.WordRepo;
import br.com.marcosoliveira20.english.repository.WordUsageLogRepo;
import br.com.marcosoliveira20.english.repository.projection.LevelCountRow;
import br.com.marcosoliveira20.english.repository.projection.WordDetailsRow;
import br.com.marcosoliveira20.english.repository.projection.WordUsageRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.Normalizer;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WordService {

    private final WordRepo wordRepo;
    private final WordUsageLogRepo wordUsageLogRepo;

    private final Clock clock = Clock.systemUTC();

    // ---------- API usada pelo controller ----------

    /**
     * Registra uso por nome. Se a Word não existir, cria globalmente.
     */
    @Transactional
    public WordUsageLog registerUsageByName(String rawName, AppUser user) {
        String name = normalizeName(rawName);
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("name vazio após normalização");
        }

        Word word = wordRepo.findByNameIgnoreCase(name)
                .orElseGet(() -> {
                    Word w = new Word();
                    w.setName(name);
                    // created_at / updated_at via @PrePersist/@PreUpdate ou default do BD
                    return null;
                });

        if (word == null) {
            throw new IllegalArgumentException("Palavra inexistente");
        }

        WordUsageLog log = new WordUsageLog();
        log.setWord(word);
        log.setUser(user);
        log.setUsedAt(LocalDateTime.now(clock));
        return wordUsageLogRepo.save(log);
    }

    /**
     * Registra uso por ID. Não cria Word.
     */
    @Transactional
    public WordUsageLog registerUsageById(Long wordId, AppUser user) {
        Word word = wordRepo.findById(wordId)
                .orElseThrow(() -> new IllegalArgumentException("Word não encontrada: " + wordId));

        WordUsageLog log = new WordUsageLog();
        log.setWord(word);
        log.setUser(user);
        log.setUsedAt(LocalDateTime.now(clock));
        return wordUsageLogRepo.save(log);
    }

    @Transactional(readOnly = true)
    public List<LevelCountDTO> countDistinctWordsByLevelForUser(String userId) {
        List<LevelCountRow> rows = wordRepo.countDistinctWordsByLevelForUser(userId);
        return rows.stream()
                .map(r -> new LevelCountDTO(r.getLevel(), r.getCnt()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<WordDetailsDTO> getDetailsForUser(Long wordId, String userId) {
        // Se a palavra não existe, o Optional do repo virá vazio (porque WHERE w.id=:wordId)
        Optional<WordDetailsRow> rowOpt = wordRepo.getDetailsForUser(wordId, userId);
        return rowOpt.map(r -> WordDetailsDTO.builder()
                .name(r.getName())
                .usedTimes(Optional.ofNullable(r.getUsedTimes()).orElse(0))
                .firstUse(r.getFirstUse())
                .lastUse(r.getLastUse())
                .build());
    }

    // ---------- utilidades privadas ----------

    /** Normaliza: remove (parênteses), remove dígitos finais (can1→can), trim, sem acentos, lowercase. */
    private String normalizeName(String raw) {
        if (!StringUtils.hasText(raw)) return "";
        String s = raw.trim();
        s = s.replaceAll("\\s*\\([^)]*\\)", ""); // remove "(...)" explicativos
        s = s.replaceAll("\\d+$", "");           // remove sufixo numérico
        s = s.replaceAll("\\s+", " ");           // colapsa espaços
        s = stripAccents(s).toLowerCase(Locale.ROOT);
        return s;
    }

    private static String stripAccents(String input) {
        String norm = Normalizer.normalize(input, Normalizer.Form.NFD);
        return norm.replaceAll("\\p{M}", "");
    }


    public List<WordUsageDTO> findByLevelAndUser(String level, String userId) {
        List<WordUsageRow> rows = wordRepo.findUsageByLevelAndUser(level, userId);
        return rows.stream()
                .map(r -> new WordUsageDTO(
                        r.getName(),
                        r.getUsed_times() == null ? 0L : r.getUsed_times(),
                        r.getFirst_use(),
                        r.getLast_use()
                ))
                .toList();
    }

}
