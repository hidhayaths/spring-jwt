package app.config;

import app.dtos.CommonResponse;
import app.utils.JWTUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class JwtTokenVerifier extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getServletPath().equals("/api/authenticate") || request.getServletPath().equals("/api/token/refresh")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(JWTUtils.HEADER_NAME);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        if(Strings.isBlank(authHeader) || !authHeader.startsWith(JWTUtils.TOKEN_PREFIX)){
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            CommonResponse<String> commonResponse = new CommonResponse<>(
                    HttpStatus.UNAUTHORIZED,
                    "unauthorized",
                    LocalDateTime.now().toString(),
                    null
            );

            new ObjectMapper().writeValue(response.getOutputStream(),commonResponse);

            return;
        }

        try {


            String authToken = authHeader.substring(JWTUtils.TOKEN_PREFIX.length());

            JWTUtils jwtUtils = JWTUtils.getInstance();

            String username  = jwtUtils.getUsernameFromToken(authToken);
            List<SimpleGrantedAuthority> roles = jwtUtils.getRolesFromToken(authToken);

            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, roles);

            SecurityContextHolder.getContext().setAuthentication(authentication);

        }catch(Exception e){
            response.setStatus(HttpStatus.FORBIDDEN.value());
            CommonResponse<String> commonResponse = new CommonResponse<>(
                    HttpStatus.FORBIDDEN,
                    e.getMessage(),
                    LocalDateTime.now().toString(),
                    null
            );

            new ObjectMapper().writeValue(response.getOutputStream(),commonResponse);
            return;
        }


        filterChain.doFilter(request,response);



    }
}
