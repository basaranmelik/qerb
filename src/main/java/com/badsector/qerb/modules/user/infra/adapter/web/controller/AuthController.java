package com.badsector.qerb.modules.user.infra.adapter.web.controller;

import com.badsector.qerb.modules.user.domain.port.in.UserUseCase;
import com.badsector.qerb.modules.user.domain.port.in.result.AuthResult;
import com.badsector.qerb.modules.user.infra.adapter.web.dto.AuthResponse;
import com.badsector.qerb.modules.user.infra.adapter.web.dto.LoginRequest;
import com.badsector.qerb.modules.user.infra.adapter.web.dto.RefreshTokenRequest;
import com.badsector.qerb.modules.user.infra.adapter.web.dto.RegisterRequest;
import com.badsector.qerb.shared.web.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication, registration, and token management")
public class AuthController {

    private final UserUseCase userUseCase;

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticates a user with email and password, returning access and refresh tokens.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials (wrong email or password)")
    })
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody @Valid LoginRequest loginRequest) {
        AuthResult result = userUseCase.login(loginRequest.toCommand());
        AuthResponse authResponse = AuthResponse.from(result);
        return ResponseEntity.ok(
                ApiResponse.success(authResponse, "Login successful")
        );
    }

    @PostMapping("/register")
    @Operation(summary = "User Registration", description = "Registers a new user and sends a verification email. The account remains disabled until verified.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User registered successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Email already exists or invalid input")
    })
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody @Valid RegisterRequest registerRequest) {
        userUseCase.register(registerRequest.toCommand());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(null, "Register successful, please check your email address"));
    }

    @GetMapping("/verify")
    @Operation(summary = "Verify Email", description = "Verifies the user's email address using the token sent via email.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Email verified successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid or expired token")
    })
    public ResponseEntity<ApiResponse<Void>> verify(
            @Parameter(description = "The verification token received in the email", required = true)
            @RequestParam String token
    ) {
        userUseCase.verifyEmail(token);
        return ResponseEntity.ok(ApiResponse.success(null, "Verify email successful"));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh Token", description = "Generates a new access token using a valid refresh token.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid or expired refresh token")
    })
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        AuthResult authResult = userUseCase.refreshToken(request.token());
        AuthResponse authResponse = AuthResponse.from(authResult);
        return ResponseEntity.ok(ApiResponse.success(authResponse, "Refresh successful"));
    }
}