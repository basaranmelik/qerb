package com.badsector.qerb.modules.user.infra.adapter.web.controller;

import com.badsector.qerb.modules.user.domain.model.Role;
import com.badsector.qerb.modules.user.domain.model.User;
import com.badsector.qerb.modules.user.domain.port.in.UserUseCase;
import com.badsector.qerb.modules.user.domain.port.in.command.ChangePasswordCommand;
import com.badsector.qerb.modules.user.domain.port.in.command.UpdateProfileCommand;
import com.badsector.qerb.modules.user.infra.adapter.web.dto.ChangePasswordRequest;
import com.badsector.qerb.modules.user.infra.adapter.web.dto.UpdateProfileRequest;
import com.badsector.qerb.modules.user.infra.adapter.web.dto.UserResponse;
import com.badsector.qerb.modules.user.infra.adapter.web.mapper.UserWebMapper;
import com.badsector.qerb.shared.web.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserUseCase userUseCase;

    @Mock
    private UserWebMapper userWebMapper;

    @InjectMocks
    private UserController userController;

    private final String MOCK_EMAIL = "test@example.com";

    @BeforeEach
    void setUp() {
        HandlerMethodArgumentResolver putPrincipal = new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.getParameterType().isAssignableFrom(UserDetails.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                          NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                return new org.springframework.security.core.userdetails.User(
                        MOCK_EMAIL, "password", Collections.emptyList());
            }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(putPrincipal)
                .build();
    }

    @Test
    @DisplayName("Get My Profile: Should return 200 OK and user data")
    void getMyProfile_ShouldReturnOk_WhenAuthenticated() throws Exception {
        User mockUser = new User();
        mockUser.setEmail(MOCK_EMAIL);
        mockUser.setFirstName("Ali");

        UserResponse mockResponse = new UserResponse(
                UUID.randomUUID(), MOCK_EMAIL, "Ali", "Veli", "123", Role.USER);

        when(userUseCase.getProfile(MOCK_EMAIL)).thenReturn(mockUser);
        when(userWebMapper.toResponse(mockUser)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value(MOCK_EMAIL))
                .andExpect(jsonPath("$.data.firstName").value("Ali"));
    }

    @Test
    @DisplayName("Update My Profile: Should return 200 OK when request is valid")
    void updateMyProfile_ShouldReturnOk_WhenRequestIsValid() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest("Mehmet", "Yılmaz", "5551234567");

        User updatedUser = new User();
        UserResponse response = new UserResponse(UUID.randomUUID(), MOCK_EMAIL, "Mehmet", "Yılmaz", "5551234567", Role.USER);

        when(userUseCase.updateProfile(any(UpdateProfileCommand.class))).thenReturn(updatedUser);
        when(userWebMapper.toResponse(updatedUser)).thenReturn(response);

        mockMvc.perform(put("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Profile updated successfully"))
                .andExpect(jsonPath("$.data.firstName").value("Mehmet"));
    }

    @Test
    @DisplayName("Update My Profile: Should return 400 Bad Request when validation fails")
    void updateMyProfile_ShouldReturn400_WhenValidationFails() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest("", "", "");

        mockMvc.perform(put("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));

        verify(userUseCase, never()).updateProfile(any());
    }

    @Test
    @DisplayName("Change Password: Should return 200 OK when password is correct")
    void changePassword_ShouldReturnOk_WhenPasswordChanged() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("OldPass123!", "NewPass123!");

        doNothing().when(userUseCase).changePassword(any(ChangePasswordCommand.class));

        mockMvc.perform(post("/api/v1/users/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Password changed successfully"));
    }

    @Test
    @DisplayName("Change Password: Should return 400 Bad Request when logic fails (Wrong old password)")
    void changePassword_ShouldReturn400_WhenOldPasswordIsWrong() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("WrongPass!", "NewPass123!");

        doThrow(new IllegalArgumentException("Wrong password"))
                .when(userUseCase).changePassword(any(ChangePasswordCommand.class));

        mockMvc.perform(post("/api/v1/users/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Wrong password"));
    }
}