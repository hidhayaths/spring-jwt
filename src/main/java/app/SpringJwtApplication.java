package app;

import app.dtos.AppRoleRequest;
import app.dtos.AppUserRequest;
import app.services.AppUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@Slf4j
public class SpringJwtApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringJwtApplication.class, args);
	}




	@Bean
	CommandLineRunner run(AppUserService userService){
		return args -> {



			userService.saveRole(new AppRoleRequest("ROLE_ADMIN","admin user"));
			userService.saveRole(new AppRoleRequest("ROLE_MANAGER","manager"));
			userService.saveRole(new AppRoleRequest("ROLE_USER","user"));
			userService.saveRole(new AppRoleRequest("ROLE_SUPER_ADMIN","super admin user"));

			userService.saveUser(new AppUserRequest("admin@springjwt.com","admin","admin123$"));
			userService.saveUser(new AppUserRequest("manager@springjwt.com","manager","manager123$"));
			userService.saveUser(new AppUserRequest("user@springjwt.com","user","user123$"));
			userService.saveUser(new AppUserRequest("superadmin@springjwt.com","super_admin","super_admin123$"));

			userService.addRoleToUser("admin@springjwt.com","ROLE_ADMIN");
			userService.addRoleToUser("manager@springjwt.com","ROLE_MANAGER");
			userService.addRoleToUser("user@springjwt.com","ROLE_USER");
			userService.addRoleToUser("superadmin@springjwt.com","ROLE_SUPER_ADMIN");

//			log.info("secret is :{}",secret);
		};
	}
}
