package com.badsector.qerb.modules.user.infra.adapter.web.dto;

import com.badsector.qerb.modules.user.domain.port.in.command.ChangePasswordCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record ChangePasswordRequest(
        @NotBlank(message = "Old password cannot be blank")
        String oldPassword,

        @NotBlank(message = "New password cannot be blank")
        @Length(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&]).+$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
        )
        String newPassword
) {
    public ChangePasswordCommand toCommand(String email) {
        return ChangePasswordCommand.builder()
                .email(email)
                .oldPassword(this.oldPassword)
                .newPassword(this.newPassword)
                .build();
    }
}
