package com.badsector.qerb.modules.user.domain.port.in.command;

public record RegisterCommand(
        String email,
        String password,
        String firstName,
        String lastName,
        String phone
) {}