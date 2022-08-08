package app.dtos;

import org.springframework.http.HttpStatus;


public record CommonResponse<T>(HttpStatus status, String message, String timeStamp, T data) {
}
