package br.com.marcosoliveira20.english.service;

import br.com.marcosoliveira20.english.model.OxfordLevel;
import br.com.marcosoliveira20.english.repository.OxfordLevelRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OxfordLevelService {

    private final OxfordLevelRepo oxfordLevelRepo;

    @Transactional(readOnly = true)
    public List<OxfordLevel> listAll() {
        return oxfordLevelRepo.findAll();
    }
}
