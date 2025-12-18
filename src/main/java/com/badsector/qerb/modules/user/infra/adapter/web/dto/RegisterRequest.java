package com.badsector.qerb.modules.user.infra.adapter.web.dto;

import com.badsector.qerb.modules.user.domain.port.in.command.RegisterCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

@Schema(description = "Data transfer object for user registration")
public record RegisterRequest(

        @Schema(
                description = "User's email address. Must be a valid email format.",
                example = "john.doe@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Please provide valid email address.")
        String email,

        @Schema(
                description = "User's password. Must be 8-64 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character.",
                example = "S3cureP@ss!",
                minLength = 8,
                maxLength = 64,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Password cannot be blank")
        @Length(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&]).+$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
        )
        String password,

        @Schema(
                description = "User's first name",
                example = "John",
                minLength = 2,
                maxLength = 50,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "First name cannot be blank")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        String firstName,

        @Schema(
                description = "User's last name",
                example = "Doe",
                minLength = 2,
                maxLength = 50,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Last name cannot be blank")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        String lastName,

        @Schema(
                description = "User's phone number. Can start with '+' and must contain 10-20 digits.",
                example = "+905551234567",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Phone number cannot be blank")
        @Size(min = 10, max = 20, message = "Phone number must be between 10 and 20 characters")
        @Pattern(regexp = "^\\+?[0-9]{10,20}$", message = "Phone number must be valid and may start with +")
        String phone
) {
    public RegisterCommand toCommand() {
        return RegisterCommand.builder()
                .email(this.email())
                .password(this.password())
                .firstName(this.firstName())
                .lastName(this.lastName())
                .phone(this.phone())
                .build();
    }
}