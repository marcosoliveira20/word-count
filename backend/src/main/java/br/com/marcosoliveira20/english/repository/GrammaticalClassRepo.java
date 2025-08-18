package br.com.marcosoliveira20.english.repository;

import br.com.marcosoliveira20.english.model.GrammaticalClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GrammaticalClassRepo extends JpaRepository<GrammaticalClass, Long> {
    Optional<GrammaticalClass> findByName(String name); // noun, verb, adjective...
}
