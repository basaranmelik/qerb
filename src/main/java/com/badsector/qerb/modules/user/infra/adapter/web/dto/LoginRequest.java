package com.badsector.qerb.modules.user.infra.adapter.web.dto;

import com.badsector.qerb.modules.user.domain.port.in.command.LoginCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest (
    @Email(message = "Please provide a valid email address")
    @NotBlank(message = "Email cannot be blank")
    @Schema(description = "User's email address", example = "john.doe@example.com")
    String email,

    @NotBlank(message = "Password cannot be blank")
    @Schema(description = "User's password", example = "SecurePass123!")
    String password
    )
{
    public LoginCommand toCommand() {
        return new LoginCommand(this.email, this.password);
    }
}