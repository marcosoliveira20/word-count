package br.com.marcosoliveira20.english.service;

import br.com.marcosoliveira20.english.model.GrammaticalClass;
import br.com.marcosoliveira20.english.repository.GrammaticalClassRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GrammaticalClassService {

    private final GrammaticalClassRepo grammaticalClassRepo;

    @Transactional(readOnly = true)
    public List<GrammaticalClass> listAll() {
        return grammaticalClassRepo.findAll();
    }

    @Transactional(readOnly = true)
    public GrammaticalClass getOrCreate(String name) {
        return grammaticalClassRepo.findByName(name)
                .orElseGet(() -> grammaticalClassRepo.save(
                        GrammaticalClass.builder().name(name).build()
                ));
    }
}
