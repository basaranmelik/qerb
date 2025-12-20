package com.badsector.qerb.modules.user.application.service;

import com.badsector.qerb.modules.user.domain.model.RefreshToken;
import com.badsector.qerb.modules.user.domain.model.Role;
import com.badsector.qerb.modules.user.domain.model.User;
import com.badsector.qerb.modules.user.domain.model.VerificationToken;
import com.badsector.qerb.modules.user.domain.port.in.command.LoginCommand;
import com.badsector.qerb.modules.user.domain.port.in.command.RegisterCommand;
import com.badsector.qerb.modules.user.domain.port.in.result.AuthResult;
import com.badsector.qerb.modules.user.domain.port.out.EmailPort;
import com.badsector.qerb.modules.user.domain.port.out.RefreshTokenPort;
import com.badsector.qerb.modules.user.domain.port.out.UserRepositoryPort;
import com.badsector.qerb.modules.user.domain.port.out.VerificationTokenPort;
import com.badsector.qerb.shared.infra.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private JwtService jwtService;
    @Mock
    private RefreshTokenPort refreshTokenPort;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private VerificationTokenPort verificationTokenPort;
    @Mock
    private EmailPort emailPort;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .password("encodedPassword")
                .firstName("Test")
                .lastName("User")
                .role(Role.USER)
                .enabled(false)
                .build();
    }

    @Test
    @DisplayName("Register: Save user and send verification when user does not exist")
    void register_ShouldSaveUserAndSendVerification_WhenUserDoesNotExist() {
        RegisterCommand cmd = new RegisterCommand("test@example.com", "password", "Test", "User", "1234567890");
        when(userRepositoryPort.existsByEmail(cmd.email())).thenReturn(false);
        when(passwordEncoder.encode(cmd.password())).thenReturn("encodedPassword");
        when(userRepositoryPort.save(any(User.class))).thenReturn(user);
        userService.register(cmd);
        verify(userRepositoryPort).save(any(User.class));
        verify(verificationTokenPort).save(any(VerificationToken.class));
        verify(emailPort).sendVerificationEmail(eq("test@example.com"), anyString());
    }

    @Test
    @DisplayName("Register: Throw exception when user already exists")
    void register_ShouldThrowException_WhenUserAlreadyExists() {
        RegisterCommand cmd = new RegisterCommand("test@example.com", "password", "Test", "User", "1234567890");
        when(userRepositoryPort.existsByEmail(cmd.email())).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> userService.register(cmd));
        verify(userRepositoryPort, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Login: Return AuthResult when credentials are valid")
    void login_ShouldReturnAuthResult_WhenCredentialsAreValid() {
        LoginCommand cmd = new LoginCommand("test@example.com", "password");
        when(userRepositoryPort.findByEmail(cmd.email())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user.getEmail())).thenReturn("accessToken");
        AuthResult result = userService.login(cmd);
        assertNotNull(result);
        assertEquals("accessToken", result.accessToken());
        assertEquals(user.getId(), result.userId());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(refreshTokenPort).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("Login: Throw exception when user not found")
    void login_ShouldThrowException_WhenUserNotFound() {
        LoginCommand cmd = new LoginCommand("unknown@example.com", "password");
        when(userRepositoryPort.findByEmail(cmd.email())).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> userService.login(cmd));
    }

    @Test
    @DisplayName("Verify Email: Enable user when token is valid")
    void verifyEmail_ShouldEnableUser_WhenTokenIsValid() {
        String tokenString = "validToken";
        VerificationToken verificationToken = VerificationToken.builder()
                .token(tokenString)
                .userId(user.getId())
                .build();
        when(verificationTokenPort.findById(tokenString)).thenReturn(Optional.of(verificationToken));
        when(userRepositoryPort.findById(user.getId())).thenReturn(Optional.of(user));
        userService.verifyEmail(tokenString);
        assertTrue(user.isEnabled());
        verify(userRepositoryPort).save(user);
        verify(verificationTokenPort).delete(tokenString);
    }

    @Test
    @DisplayName("Verify Email: Throw exception when token is invalid")
    void verifyEmail_ShouldThrowException_WhenTokenIsInvalid() {
        String tokenString = "invalidToken";
        when(verificationTokenPort.findById(tokenString)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> userService.verifyEmail(tokenString));
    }

    @Test
    @DisplayName("Refresh Token: Return new tokens when old refresh token is valid")
    void refreshToken_ShouldReturnNewTokens_WhenOldRefreshTokenIsValid() {
        String oldTokenString = "oldRefreshToken";
        RefreshToken oldToken = RefreshToken.builder()
                .token(oldTokenString)
                .userId(user.getId())
                .build();
        when(refreshTokenPort.findByToken(oldTokenString)).thenReturn(Optional.of(oldToken));
        when(userRepositoryPort.findById(user.getId())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user.getEmail())).thenReturn("newAccessToken");
        AuthResult result = userService.refreshToken(oldTokenString);
        assertNotNull(result);
        assertEquals("newAccessToken", result.accessToken());
        verify(refreshTokenPort).delete(oldTokenString);
        verify(refreshTokenPort).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("Refresh Token: Throw exception when token is invalid")
    void refreshToken_ShouldThrowException_WhenTokenIsInvalid() {
        String oldTokenString = "invalidRefreshToken";
        when(refreshTokenPort.findByToken(oldTokenString)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> userService.refreshToken(oldTokenString));
    }

    @Test
    @DisplayName("Verify Email: Do nothing when user is already enabled")
    void verifyEmail_ShouldDoNothing_WhenUserIsAlreadyEnabled() {
        String tokenString = "validToken";
        VerificationToken verificationToken = VerificationToken.builder()
                .token(tokenString)
                .userId(user.getId())
                .build();
        User enabledUser = User.builder().id(user.getId()).enabled(true).build();
        when(verificationTokenPort.findById(tokenString)).thenReturn(Optional.of(verificationToken));
        when(userRepositoryPort.findById(user.getId())).thenReturn(Optional.of(enabledUser));
        userService.verifyEmail(tokenString);
        verify(userRepositoryPort, never()).save(any(User.class));
        verify(verificationTokenPort, never()).delete(anyString());
    }

    @Test
    @DisplayName("Verify Email: Throw exception when token exists but user not found")
    void verifyEmail_ShouldThrowException_WhenTokenExistsButUserNotFound() {
        String tokenString = "validToken";
        VerificationToken verificationToken = VerificationToken.builder()
                .token(tokenString)
                .userId(UUID.randomUUID())
                .build();

        when(verificationTokenPort.findById(tokenString)).thenReturn(Optional.of(verificationToken));
        when(userRepositoryPort.findById(any(UUID.class))).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.verifyEmail(tokenString));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("Login: Throw BadCredentialsException when authentication fails")
    void login_ShouldThrowBadCredentials_WhenAuthenticationFails() {
        LoginCommand cmd = new LoginCommand("test@example.com", "wrongPass");
        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager).authenticate(any());
        assertThrows(BadCredentialsException.class,
                () -> userService.login(cmd));
        verify(userRepositoryPort, never()).findByEmail(anyString());
    }
}