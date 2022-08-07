package app.dtos;

import javax.validation.constraints.NotBlank;

public record AppRoleRequest(@NotBlank(message = "role name cannot be empty") String name, String description) {
}
