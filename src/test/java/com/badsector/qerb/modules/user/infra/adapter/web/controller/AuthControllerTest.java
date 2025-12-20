package com.badsector.qerb.modules.user.infra.adapter.web.controller;

import com.badsector.qerb.modules.user.domain.port.in.UserUseCase;
import com.badsector.qerb.modules.user.domain.port.in.command.LoginCommand;
import com.badsector.qerb.modules.user.domain.port.in.command.RegisterCommand;
import com.badsector.qerb.modules.user.domain.port.in.result.AuthResult;
import com.badsector.qerb.modules.user.infra.adapter.web.dto.LoginRequest;
import com.badsector.qerb.modules.user.infra.adapter.web.dto.RefreshTokenRequest;
import com.badsector.qerb.modules.user.infra.adapter.web.dto.RegisterRequest;
import com.badsector.qerb.shared.web.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserUseCase userUseCase;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Register: Should return 201 Created when request is valid")
    void register_ShouldReturnCreated_WhenRequestIsValid() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "test@example.com", "S3cureP@ss!", "Test", "User", "+1234567890");

        doNothing().when(userUseCase).register(any(RegisterCommand.class));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Register successful, please check your email address"));
    }

    @Test
    @DisplayName("Register: Should return 400 Bad Request when email is invalid")
    void register_ShouldReturn400_WhenEmailIsInvalid() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "invalid-email", "123", "", "", "");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));

        verify(userUseCase, never()).register(any());
    }

    @Test
    @DisplayName("Login: Should return 200 OK and tokens when credentials are valid")
    void login_ShouldReturnOk_WhenCredentialsAreValid() throws Exception {
        LoginRequest request = new LoginRequest("test@example.com", "S3cureP@ss!");
        AuthResult authResult = AuthResult.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .userId(UUID.randomUUID())
                .build();

        when(userUseCase.login(any(LoginCommand.class))).thenReturn(authResult);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.data.userId").exists());
    }

    @Test
    @DisplayName("Login: Should return 401 Unauthorized when credentials are invalid")
    void login_ShouldReturn401_WhenCredentialsAreInvalid() throws Exception {
        LoginRequest request = new LoginRequest("test@example.com", "WrongPass");

        when(userUseCase.login(any(LoginCommand.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid email or password."));
    }

    @Test
    @DisplayName("Verify: Should return 200 OK when token is valid")
    void verify_ShouldReturnOk_WhenTokenIsValid() throws Exception {
        String token = "valid-token";
        doNothing().when(userUseCase).verifyEmail(token);

        mockMvc.perform(get("/api/v1/auth/verify")
                        .param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Verify email successful"));
    }

    @Test
    @DisplayName("Refresh: Should return 200 OK and new access token")
    void refresh_ShouldReturnOk_WhenTokenIsValid() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");
        AuthResult authResult = AuthResult.builder()
                .accessToken("newAccessToken")
                .refreshToken("newRefreshToken")
                .userId(UUID.randomUUID())
                .build();

        when(userUseCase.refreshToken(anyString())).thenReturn(authResult);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("newAccessToken"));
    }
}