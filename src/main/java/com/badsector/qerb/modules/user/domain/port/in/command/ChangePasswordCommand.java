package com.badsector.qerb.modules.user.domain.port.in.command;

import lombok.Builder;

@Builder
public record ChangePasswordCommand(
        String email,
        String oldPassword,
        String newPassword
) {
}
