package com.badsector.qerb.modules.user.infra.adapter.persistence;

import com.badsector.qerb.modules.user.domain.model.VerificationToken;
import com.badsector.qerb.modules.user.domain.port.out.VerificationTokenPort;
import com.badsector.qerb.modules.user.infra.adapter.persistence.entity.VerificationTokenRedisEntity;
import com.badsector.qerb.modules.user.infra.adapter.persistence.repository.VerificationTokenRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VerificationTokenRedisAdapter implements VerificationTokenPort {
    private final VerificationTokenRedisRepository verificationTokenRedisRepository;

    @Override
    public void save(VerificationToken domain) {
        verificationTokenRedisRepository.save(VerificationTokenRedisEntity.builder()
                .token(domain.getToken())
                .userId(domain.getUserId())
                .build());
    }

    @Override
    public Optional<VerificationToken> findById(String token) {
        return verificationTokenRedisRepository.findById(token)
                .map(entity -> VerificationToken.builder()
                        .token(entity.getToken())
                        .userId(entity.getUserId())
                        .build());
    }

    @Override
    public void delete(String token) {
        verificationTokenRedisRepository.deleteById(token);
    }
}
