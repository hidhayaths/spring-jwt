package app.controllers;

import app.dtos.*;
import app.models.AppRole;
import app.models.AppUser;
import app.services.AppUserService;
import app.utils.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private AppUserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<AppUser>> getUsers(){
        return ResponseEntity.ok(userService.getUsers());
    }

    @PostMapping("/user/save")
    public ResponseEntity<AppUser> saveUser(@Valid @RequestBody AppUserRequest userRequest){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveUser(userRequest));
    }

    @PostMapping("/role/save")
    public ResponseEntity<AppRole> saveRole(@Valid @RequestBody AppRoleRequest roleRequest){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveRole(roleRequest));
    }

    @PostMapping("/role/add-to-user")
    public ResponseEntity<String> addRoleToUser(@Valid @RequestBody RoleUserRequest roleUserRequest){
        userService.addRoleToUser(roleUserRequest.email(), roleUserRequest.roleName());
        return ResponseEntity.ok("success");
    }

    @PostMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authHeader = request.getHeader(JWTUtil.HEADER_NAME);

        if(authHeader.isBlank())
            throw new RuntimeException("Authorization Header is missing");

        if(!authHeader.startsWith(JWTUtil.TOKEN_PREFIX))
            throw new RuntimeException("Authorization token is not Bearer token");

        JWTUtil jwtUtil = JWTUtil.getInstance();

        String authToken = authHeader.substring(JWTUtil.TOKEN_PREFIX.length());

        if(jwtUtil.isTokenExpired(authToken))
            throw new RuntimeException("refresh token is already expired");

        String userEmail = jwtUtil.getUsernameFromToken(authToken);

        AppUser appUser = userService.findUser(userEmail).orElseThrow(
                ()-> new RuntimeException("user '"+userEmail+"' does not exists, invalid user")
        );

        List<GrantedAuthority> roles = appUser.getRoles().stream().map(AppRole::getName).map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        TokenResponse tokenResponse = jwtUtil.getToken(TokenType.ACCESS_TOKEN, appUser.getEmail(), roles);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.OK.value());

        new ObjectMapper().writeValue(response.getOutputStream(),tokenResponse);

    }

    @GetMapping("/welcome")
    public ResponseEntity<CommonResponse> greetUser(){
        String loggedInUser = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(
                new CommonResponse(
                        HttpStatus.OK,
                        "success",
                        LocalDateTime.now().toString(),
                        "welcome "+ loggedInUser
                )
        );
    }


}
