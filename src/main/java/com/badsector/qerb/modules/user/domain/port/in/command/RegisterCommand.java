package com.badsector.qerb.modules.user.domain.port.in.command;

import lombok.Builder;
import lombok.Data;

@Builder
public record RegisterCommand(
        String email,
        String password,
        String firstName,
        String lastName,
        String phone
) {}