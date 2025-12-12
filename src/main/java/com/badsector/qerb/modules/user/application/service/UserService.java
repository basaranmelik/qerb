package com.badsector.qerb.modules.user.application.service;

import com.badsector.qerb.modules.user.api.dto.AuthResponse;
import com.badsector.qerb.modules.user.api.dto.LoginRequest;
import com.badsector.qerb.modules.user.api.dto.RegisterRequest;
import com.badsector.qerb.modules.user.domain.Role;
import com.badsector.qerb.modules.user.domain.User;
import com.badsector.qerb.modules.user.domain.repository.UserRepository;
import com.badsector.qerb.modules.user.infra.security.JwtService;
import com.badsector.qerb.modules.user.infra.security.SecurityUser;
import io.jsonwebtoken.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email()))
            throw new IllegalArgumentException("Email already in use");
        if (userRepository.existsByPhone(req.phone()))
            throw new IllegalArgumentException("Phone number already in use");
        User newUser = User.builder()
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .firstName(req.firstName())
                .lastName(req.lastName())
                .phone(req.phone())
                .role(Role.USER)
                .enabled(false)
                .deleted(false)
                .build();
        User savedUser = userRepository.save(newUser);
        String token = jwtService.generateToken(savedUser.getEmail());
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));

        SecurityUser securityUser = new SecurityUser(user);
        String token = jwtService.generateToken(securityUser.getUsername());

        return new AuthResponse(token);
    }
}

