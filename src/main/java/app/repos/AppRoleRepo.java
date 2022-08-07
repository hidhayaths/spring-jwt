package app.repos;

import app.models.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppRoleRepo extends JpaRepository<AppRole,String> {

    Optional<AppRole> findByName(String name);

    Boolean existsByName(String name);

}
