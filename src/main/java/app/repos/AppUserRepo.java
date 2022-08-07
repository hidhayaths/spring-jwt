package app.repos;

import app.models.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepo extends JpaRepository<AppUser,String> {

    Optional<AppUser> findByEmail(String email);

    Boolean existsByEmail(String email);
}
