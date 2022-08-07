package app.services;

import app.dtos.AppRoleRequest;
import app.dtos.AppUserRequest;
import app.models.AppRole;
import app.models.AppUser;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface AppUserService extends UserDetailsService {

    AppUser saveUser(AppUserRequest userRequest);

    AppRole saveRole(AppRoleRequest roleRequest);

    void addRoleToUser(String email,String roleName);

    Optional<AppUser> findUser(String email);

    List<AppUser> getUsers();
}
