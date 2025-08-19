package br.com.marcosoliveira20.english.repository;

import br.com.marcosoliveira20.english.model.Word;
import br.com.marcosoliveira20.english.repository.projection.LevelCountRow;
import br.com.marcosoliveira20.english.repository.projection.WordDetailsRow;
import br.com.marcosoliveira20.english.repository.projection.WordUsageRow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface WordRepo extends JpaRepository<Word, Long> {


    Optional<Word> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    List<Word> findTop20ByNameStartingWithIgnoreCaseOrderByNameAsc(String prefix);

    Page<Word> findByNameContainingIgnoreCase(String query, Pageable pageable);

    @Query(value = """
        SELECT
          w.name                AS name,
          s.used_times          AS used_times,
          DATE(s.first_use)     AS first_use,
          DATE(s.last_use)      AS last_use
        FROM v_word_usage_stats s
        JOIN word w                ON w.id = s.word_id
        JOIN word_oxford_level wol ON wol.word_id = w.id
        JOIN oxford_level ol       ON ol.id = wol.oxford_level_id
        WHERE s.user_id = :userId
          AND ol.name = :level
        ORDER BY w.name
        """, nativeQuery = true)
    List<WordUsageRow> findUsageByLevelAndUser(@Param("level") String level,
                                               @Param("userId") String userId);

    // ========= NOVAS QUERIES ISOLADAS =========

    /**
     * Conta palavras distintas por nível (A1..C2) para um usuário.
     * Mantém a linha do nível mesmo quando count=0 (LEFT JOIN).
     */
    @Query(value = """
        SELECT l.name AS level,
               COALESCE(agg.cnt, 0) AS cnt
        FROM oxford_level l
        LEFT JOIN (
            SELECT wol.oxford_level_id AS level_id,
                   COUNT(DISTINCT wul.word_id) AS cnt
            FROM word_usage_log wul
            JOIN word_oxford_level wol ON wol.word_id = wul.word_id
            WHERE wul.user_id = :userId
            GROUP BY wol.oxford_level_id
        ) agg ON agg.level_id = l.id
        ORDER BY FIELD(l.name,'A1','A2','B1','B2','C1','C2'), l.name
        """, nativeQuery = true)
    List<LevelCountRow> countDistinctWordsByLevelForUser(@Param("userId") String userId);

    /**
     * Detalhe da palavra + métricas de uso do usuário.
     * Se a palavra existir mas não houver uso, retorna used_times=0 e datas NULL.
     * Se a palavra não existir, retorna vazio (Optional.empty no service).
     */
    @Query(value = """
        SELECT
            w.name                                 AS name,
            COALESCE(stats.used_times, 0)          AS used_times,
            stats.first_use                        AS first_use,
            stats.last_use                         AS last_use
        FROM word w
        LEFT JOIN (
            SELECT
                word_id,
                COUNT(*)             AS used_times,
                MIN(used_at)         AS first_use,
                MAX(used_at)         AS last_use
            FROM word_usage_log
            WHERE user_id = :userId
              AND word_id = :wordId
            GROUP BY word_id
        ) stats ON stats.word_id = w.id
        WHERE w.id = :wordId
        """, nativeQuery = true)
    Optional<WordDetailsRow> getDetailsForUser(@Param("wordId") Long wordId,
                                               @Param("userId") String userId);
}
