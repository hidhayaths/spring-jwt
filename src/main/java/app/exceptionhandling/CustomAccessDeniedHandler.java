package app.exceptionhandling;

import app.dtos.CommonResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex) throws IOException, ServletException {
        log.error("access denied exception occurred");

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        response.setStatus(HttpStatus.FORBIDDEN.value());
        CommonResponse<String> commonResponse = new CommonResponse<>(
                HttpStatus.FORBIDDEN,
                ex.getMessage(),
                LocalDateTime.now().toString(),
                null
        );

        new ObjectMapper().writeValue(response.getOutputStream(),commonResponse);
    }
}
