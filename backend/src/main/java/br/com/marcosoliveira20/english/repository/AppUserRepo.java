package br.com.marcosoliveira20.english.repository;

import br.com.marcosoliveira20.english.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepo extends JpaRepository<AppUser, String> {
}
