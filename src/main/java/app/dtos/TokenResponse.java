package app.dtos;

import java.time.Instant;
import java.util.Date;

public record TokenResponse(TokenType tokenType, String token, Date expiresAt, String expirationDate) {

}
