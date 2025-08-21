package br.com.marcosoliveira20.english.repository;

import br.com.marcosoliveira20.english.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppUserRepo extends JpaRepository<AppUser, String> {
    List<AppUser> getAppUserById(String id);
}
