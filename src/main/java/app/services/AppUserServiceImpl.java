package app.services;

import app.dtos.AppRoleRequest;
import app.dtos.AppUserRequest;
import app.models.AppRole;
import app.models.AppUser;
import app.repos.AppRoleRepo;
import app.repos.AppUserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class AppUserServiceImpl implements AppUserService{

    @Autowired
    private AppUserRepo userRepo;

    @Autowired
    private AppRoleRepo roleRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser appUser = userRepo.findByEmail(email).orElseThrow(
                ()-> new UsernameNotFoundException("user '"+email+"' does not exists")
        );

        Set<SimpleGrantedAuthority> authorities = appUser.getRoles().stream().map(AppRole::getName).map(SimpleGrantedAuthority::new).collect(Collectors.toSet());

        return new User(appUser.getEmail(), appUser.getPassword(),authorities);
    }

    @Override
    public AppUser saveUser(AppUserRequest userRequest) {
        log.info("saving new user:{}",userRequest.email());

        if(userRepo.existsByEmail(userRequest.email())) {
            log.error("Email:{} is already registered",userRequest.email());
            throw new IllegalStateException("Email:" + userRequest.email() + " is already registered");
        }

        AppRole userRole = roleRepo.findByName("ROLE_USER").orElse(new AppRole(null,"ROLE_USER","user role"));

        AppUser user = new AppUser();
        user.setEmail(userRequest.email());
        user.setName(userRequest.name());
        user.setPassword(passwordEncoder.encode(userRequest.password()));
        user.setRoles(List.of(userRole));

        return userRepo.save(user);
    }

    @Override
    public AppRole saveRole(AppRoleRequest roleRequest) {
        log.info("adding new role:{}",roleRequest.name());


        if(roleRepo.existsByName(roleRequest.name())) {
            log.error("Role:{} is already exists",roleRequest.name());
            throw new IllegalStateException("Role :" + roleRequest.name() + " is already exists");
        }

        AppRole role = new AppRole(null,roleRequest.name(),roleRequest.description());

        return roleRepo.save(role);
    }

    @Override
    public void addRoleToUser(String email, String roleName) {
        log.info("adding role '{}' to user '{}'",roleName,email);
        AppUser user = userRepo.findByEmail(email).orElseThrow(()->new IllegalStateException("user with email '"+email+"' does not exists"));
        AppRole role = roleRepo.findByName(roleName).orElseThrow(()-> new IllegalStateException("role '"+roleName+" is not a valid input"));

        if(!user.getRoles().contains(role))
            user.getRoles().add(role);
    }

    @Override
    public Optional<AppUser> findUser(String email) {
        log.info("finding user '{}'",email);
        return userRepo.findByEmail(email);
    }

    @Override
    public List<AppUser> getUsers() {
        log.info("fetching all users...");
        return userRepo.findAll();
    }


}
