package app.dtos;

public record LoginResponse(String message, TokenResponse accessToken, TokenResponse refreshToken) {
}
