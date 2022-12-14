package app.config;

import app.dtos.LoginRequest;
import app.dtos.LoginResponse;
import app.dtos.TokenResponse;
import app.dtos.TokenType;
import app.utils.JWTUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try{

            LoginRequest loginRequest = new ObjectMapper().readValue(request.getInputStream(),LoginRequest.class);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.email(),loginRequest.password());
            return authenticationManager.authenticate(authenticationToken);

        }catch (IOException ex){
            throw new RuntimeException(ex);
        }


    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {


        User user = (User) authResult.getPrincipal();

        JWTUtils jwtUtils = JWTUtils.getInstance();

        TokenResponse accessToken = jwtUtils.getToken(TokenType.ACCESS_TOKEN,user);
        TokenResponse refreshToken = jwtUtils.getToken(TokenType.REFRESH_TOKEN,user);

        LoginResponse loginResponse = new LoginResponse("success", accessToken,refreshToken);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        new ObjectMapper().writeValue(response.getOutputStream(),loginResponse);

    }
}
