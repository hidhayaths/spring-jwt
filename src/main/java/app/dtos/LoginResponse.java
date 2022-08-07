package app.dtos;

import java.util.Date;

public record LoginResponse(String message, TokenResponse accessToken, TokenResponse refreshToken) {
}
