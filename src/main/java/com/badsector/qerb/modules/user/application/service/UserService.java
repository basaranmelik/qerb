package com.badsector.qerb.modules.user.application.service;

import com.badsector.qerb.modules.user.domain.model.RefreshToken;
import com.badsector.qerb.modules.user.domain.model.Role;
import com.badsector.qerb.modules.user.domain.model.User;
import com.badsector.qerb.modules.user.domain.model.VerificationToken;
import com.badsector.qerb.modules.user.domain.port.in.UserUseCase;
import com.badsector.qerb.modules.user.domain.port.in.command.ChangePasswordCommand;
import com.badsector.qerb.modules.user.domain.port.in.command.LoginCommand;
import com.badsector.qerb.modules.user.domain.port.in.command.RegisterCommand;
import com.badsector.qerb.modules.user.domain.port.in.command.UpdateProfileCommand;
import com.badsector.qerb.modules.user.domain.port.in.result.AuthResult;
import com.badsector.qerb.modules.user.domain.port.out.*;
import com.badsector.qerb.shared.domain.exception.*;
import com.badsector.qerb.shared.infra.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final JwtService jwtService;
    private final RefreshTokenPort refreshTokenPort;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenPort verificationTokenPort;
    private final EmailPort emailPort;
    private final PasswordResetTokenPort passwordResetTokenPort;
    private final TokenBlacklistPort tokenBlacklistPort;

    @Override
    @Transactional
    public void register(RegisterCommand cmd) {
        if (userRepositoryPort.existsByEmail(cmd.email())) {
            throw new ConflictException("User with email " + cmd.email() + " already exists");
        }
        if (userRepositoryPort.existsByPhone(cmd.phone())) {
            throw new ConflictException("User with phone " + cmd.phone() + " already exists");
        }

        User newUser = User.builder()
                .email(cmd.email())
                .password(passwordEncoder.encode(cmd.password()))
                .firstName(cmd.firstName())
                .lastName(cmd.lastName())
                .phone(cmd.phone())
                .role(Role.USER)
                .enabled(false)
                .deleted(false)
                .build();

        User savedUser = userRepositoryPort.save(newUser);

        sendVerificationFlow(savedUser);
    }

    @Override
    public AuthResult login(LoginCommand cmd) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(cmd.email(), cmd.password()));

        User user = userRepositoryPort.findByEmail(cmd.email())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", cmd.email()));

        return generateTokens(user);
    }

    @Override
    @Transactional
    public void verifyEmail(String tokenString) {
        VerificationToken verificationToken = verificationTokenPort.findById(tokenString)
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired verification token"));

        User user = userRepositoryPort.findById(verificationToken.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", verificationToken.getUserId()));

        if (user.isEnabled()) {
            return;
        }

        user.setEnabled(true);
        userRepositoryPort.save(user);
        verificationTokenPort.delete(tokenString);
    }

    @Override
    @Transactional
    public AuthResult refreshToken(String oldRefreshTokenString) {
        RefreshToken storedToken = refreshTokenPort.findByToken(oldRefreshTokenString)
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired refresh token"));

        User user = userRepositoryPort.findById(storedToken.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", storedToken.getUserId()));

        refreshTokenPort.delete(oldRefreshTokenString);
        return generateTokens(user);
    }

    private AuthResult generateTokens(User user) {
        String accessToken = jwtService.generateToken(user.getEmail());
        String refreshTokenString = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenString)
                .userId(user.getId())
                .build();

        refreshTokenPort.save(refreshToken);

        return AuthResult.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenString)
                .userId(user.getId())
                .build();
    }


    private void sendVerificationFlow(User user) {
        String tokenString = UUID.randomUUID().toString();

        VerificationToken verificationToken = VerificationToken.builder()
                .token(tokenString)
                .userId(user.getId())
                .build();

        verificationTokenPort.save(verificationToken);
        emailPort.sendVerificationEmail(user.getEmail(), tokenString);
    }

    @Override
    @Transactional
    public void forgotPassword(String email) {
        if (!userRepositoryPort.existsByEmail(email)) {
            return;
        }

        String token = UUID.randomUUID().toString();
        passwordResetTokenPort.save(token, email);
        emailPort.sendPasswordResetEmail(email, token);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        String email = passwordResetTokenPort.findEmailByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired password reset link"));

        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepositoryPort.save(user);
        passwordResetTokenPort.delete(token);
    }

    @Override
    public void logout(String authHeader, String refreshToken) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        String accessToken = authHeader.substring(7);
        try {
            Date expirationDate = jwtService.extractExpiration(accessToken);
            long currentTime = System.currentTimeMillis();
            long ttl = expirationDate.getTime() - currentTime;

            if (ttl > 0) {
                tokenBlacklistPort.blacklistToken(accessToken, ttl);
            }
        } catch (Exception e) {
            // Token zaten geçersizse logout işlemine engel olma
        }

        if (refreshToken != null && !refreshToken.isEmpty()) {
            refreshTokenPort.delete(refreshToken);
        }
    }

    @Override
    public User getProfile(String email) {
        return userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }


    @Override
    @Transactional
    public User updateProfile(UpdateProfileCommand cmd) {
        User user = userRepositoryPort.findByEmail(cmd.email())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", cmd.email()));

        if (cmd.firstName() != null && !cmd.firstName().isBlank()) {
            user.setFirstName(cmd.firstName());
        }

        if (cmd.lastName() != null && !cmd.lastName().isBlank()) {
            user.setLastName(cmd.lastName());
        }

        if (cmd.phone() != null && !cmd.phone().isBlank()) {
            user.setPhone(cmd.phone());
        }

        return userRepositoryPort.save(user);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordCommand cmd) {
        User user = userRepositoryPort.findByEmail(cmd.email())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", cmd.email()));

        if (!passwordEncoder.matches(cmd.oldPassword(), user.getPassword())) {
            throw new PasswordMismatchException("Wrong password provided");
        }

        user.setPassword(passwordEncoder.encode(cmd.newPassword()));
        userRepositoryPort.save(user);
    }
}