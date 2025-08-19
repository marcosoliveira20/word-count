package br.com.marcosoliveira20.english.repository;

import br.com.marcosoliveira20.english.model.WordUsageLog;
import br.com.marcosoliveira20.english.model.Word;
import br.com.marcosoliveira20.english.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface WordUsageLogRepo extends JpaRepository<WordUsageLog, Long> {

    List<WordUsageLog> findByWordAndUserOrderByUsedAtDesc(Word word, AppUser user);



    @Query("""
    SELECT COUNT(DISTINCT wul.word.id)
    FROM WordUsageLog wul
    WHERE wul.user.id = :userId
""")
    long countDistinctWordsByUserId(@Param("userId") String userId);

    List<WordUsageLog> findByUserAndUsedAtBetween(AppUser user, LocalDateTime from, LocalDateTime to);
}
