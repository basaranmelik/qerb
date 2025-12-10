package com.badsector.qerb.modules.user.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

public record RegisterRequest(
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Please provide valid email address.")
        String email,

        @NotBlank(message = "Password cannot be blank")
        @Length(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&]).+$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
        )
        String password,

        @NotBlank(message = "First name cannot be blank")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        String firstName,

        @NotBlank(message = "Last name cannot be blank")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        String lastName,

        @NotBlank(message = "Phone number cannot be blank")
        @Size(min = 10, max = 20, message = "Phone number must be between 10 and 20 characters")
        // TODO validasyon mesajını ve regex ifadesini düzenle
        @Pattern(regexp = "^\\+?[0-9]{10,20}$", message = "Phone number must be valid and may start with +")
        String phone
) {}

