package com.badsector.qerb.modules.user.domain.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class VerificationToken {
    private String token;
    private UUID userId;
}
