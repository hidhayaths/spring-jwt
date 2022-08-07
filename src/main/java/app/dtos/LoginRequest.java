package app.dtos;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public record LoginRequest(
        @Email String email, @NotBlank String password) {
}
