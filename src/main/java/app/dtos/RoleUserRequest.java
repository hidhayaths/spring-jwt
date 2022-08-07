package app.dtos;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public record RoleUserRequest(@Email String email, @NotBlank String roleName) {
}
