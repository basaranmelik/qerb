package com.badsector.qerb.modules.user.domain.port.in.command;

public record LoginCommand (
        String email,
        String password
) {}
