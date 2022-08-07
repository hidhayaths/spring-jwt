package app.dtos;

import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;


public record CommonResponse<T>(HttpStatus status, String message, String timeStamp, T data) {
}
