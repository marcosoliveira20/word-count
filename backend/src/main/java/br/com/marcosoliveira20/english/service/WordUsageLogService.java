package br.com.marcosoliveira20.english.service;

import br.com.marcosoliveira20.english.model.AppUser;
import br.com.marcosoliveira20.english.model.Word;
import br.com.marcosoliveira20.english.model.WordUsageLog;
import br.com.marcosoliveira20.english.repository.WordUsageLogRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WordUsageLogService {

    private final WordUsageLogRepo wordUsageLogRepo;

    @Transactional(readOnly = true)
    public long countUsageByUser(AppUser user) {
        return wordUsageLogRepo.countDistinctWordsByUserId(user.getId());
    }

    @Transactional(readOnly = true)
    public List<WordUsageLog> history(Word word, AppUser user) {
        return wordUsageLogRepo.findByWordAndUserOrderByUsedAtDesc(word, user);
    }
}
