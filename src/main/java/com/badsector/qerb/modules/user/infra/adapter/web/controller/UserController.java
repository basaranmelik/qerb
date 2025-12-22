package com.badsector.qerb.modules.user.infra.adapter.web.controller;

import com.badsector.qerb.modules.user.domain.model.User;
import com.badsector.qerb.modules.user.domain.port.in.UserUseCase;
import com.badsector.qerb.modules.user.domain.port.in.command.ChangePasswordCommand;
import com.badsector.qerb.modules.user.domain.port.in.command.UpdateProfileCommand;
import com.badsector.qerb.modules.user.infra.adapter.web.dto.ChangePasswordRequest;
import com.badsector.qerb.modules.user.infra.adapter.web.dto.UpdateProfileRequest;
import com.badsector.qerb.modules.user.infra.adapter.web.dto.UserResponse;
import com.badsector.qerb.modules.user.infra.adapter.web.mapper.UserWebMapper;
import com.badsector.qerb.shared.web.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for user profile management")
@SecurityRequirement(name = "bearerAuth") // Tüm endpointler için kilit simgesi ekler (Token gerekli)
public class UserController {

    private final UserUseCase userUseCase;
    private final UserWebMapper userWebMapper;

    @GetMapping("/me")
    @Operation(summary = "Get My Profile", description = "Retrieves the profile information of the currently authenticated user.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized (Invalid or missing token)")
    })
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userUseCase.getProfile(userDetails.getUsername());
        UserResponse response = userWebMapper.toResponse(user);
        return ResponseEntity.ok(ApiResponse.success(response, "Profile retrieved successfully"));
    }

    @PutMapping("/me")
    @Operation(summary = "Update My Profile", description = "Updates profile information (Name, Surname, Phone).")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profile updated successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data (e.g., name too short)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<UserResponse>> updateMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid UpdateProfileRequest request
    ) {
        UpdateProfileCommand command = request.toCommand(userDetails.getUsername());
        User updatedUser = userUseCase.updateProfile(command);
        UserResponse response = userWebMapper.toResponse(updatedUser);
        return ResponseEntity.ok(ApiResponse.success(response, "Profile updated successfully"));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change Password", description = "Changes the password for the logged-in user. Requires old password verification.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input or wrong old password"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid ChangePasswordRequest request
    ) {
        ChangePasswordCommand cmd = request.toCommand(userDetails.getUsername());
        userUseCase.changePassword(cmd);
        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
    }
}

