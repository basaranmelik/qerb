package com.badsector.qerb.modules.user.infra.adapter.web.dto;

import com.badsector.qerb.modules.user.domain.port.in.command.UpdateProfileCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        String firstName,

        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        String lastName,

        @NotBlank(message = "Phone number cannot be blank")
        @Size(min = 10, max = 20, message = "Phone number must be between 10 and 20 characters")
        @Pattern(regexp = "^\\+?[0-9]{10,20}$", message = "Phone number must be valid and may start with +")
        String phone
) {
    public UpdateProfileCommand toCommand(String email) {
        return UpdateProfileCommand.builder()
                .email(email)
                .firstName(this.firstName)
                .lastName(this.lastName)
                .phone(this.phone)
                .build();
    }
}
