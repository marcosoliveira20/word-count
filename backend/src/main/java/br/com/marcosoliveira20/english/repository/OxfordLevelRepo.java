package br.com.marcosoliveira20.english.repository;

import br.com.marcosoliveira20.english.model.OxfordLevel; // <- @Entity
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
public interface OxfordLevelRepo extends JpaRepository<OxfordLevel, Long> {

    interface LevelCountView {
        String getLevel();
        Long getCount();
    }

    @Query(value = """
    SELECT l.name AS level,
           COALESCE(agg.cnt, 0) AS count
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
    List<LevelCountView> countDistinctByLevelForUser(@Param("userId") String userId);

}
