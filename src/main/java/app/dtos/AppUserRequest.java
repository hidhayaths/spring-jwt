package app.dtos;



import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


public record AppUserRequest(
        @Email(message = "invalid Email")
        String email,

        @NotBlank(message = "name cannot be blank")
        String name,

        @Length(min=3,max = 12)
        @NotBlank(message = "password cannot be empty")
        String password
        ) {


}
