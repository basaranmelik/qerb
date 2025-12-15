package com.badsector.qerb.modules.user.application.service;

import com.badsector.qerb.modules.user.domain.model.RefreshToken;
import com.badsector.qerb.modules.user.domain.model.Role;
import com.badsector.qerb.modules.user.domain.model.User;
import com.badsector.qerb.modules.user.domain.port.in.UserUseCase;
import com.badsector.qerb.modules.user.domain.port.in.result.AuthResult;
import com.badsector.qerb.modules.user.domain.port.out.RefreshTokenPort; // 🆕 Redis Portu
import com.badsector.qerb.modules.user.domain.port.out.UserRepositoryPort;
import com.badsector.qerb.modules.user.infra.adapter.web.dto.AuthResponse;
import com.badsector.qerb.modules.user.infra.adapter.web.dto.LoginRequest;
import com.badsector.qerb.modules.user.infra.adapter.web.dto.RegisterRequest;
import com.badsector.qerb.shared.infra.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase {
    @Override
    public AuthResult register(User user) {
        return null;
    }

    @Override
    public AuthResult login(User user) {
        return null;
    }

}