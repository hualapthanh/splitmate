package com.pm.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.userservice.dto.request.UpdateAvatarRequest;
import com.pm.userservice.dto.request.UpdateProfileRequest;
import com.pm.userservice.dto.response.ProfileResponse;
import com.pm.userservice.dto.response.PublicProfileResponse;
import com.pm.userservice.exception.GlobalExceptionHandler;
import com.pm.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UUID sampleUserId;

    @BeforeEach
    void setUp() {
        sampleUserId = UUID.randomUUID();
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/users/{id} should return public profile without authentication")
    void getPublicProfile_shouldReturn200() throws Exception {
        PublicProfileResponse response = PublicProfileResponse.builder()
                .userId(sampleUserId)
                .fullName("John Doe")
                .avatarUrl("https://example.com/john.jpg")
                .bio("Public Bio")
                .build();

        when(userService.getPublicProfile(sampleUserId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/{id}", sampleUserId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("John Doe"))
                .andExpect(jsonPath("$.bio").value("Public Bio"));
    }

    @Test
    @DisplayName("PUT /api/v1/users/avatar should accept valid avatar URL")
    void updateAvatar_shouldReturn200() throws Exception {
        UpdateAvatarRequest request = UpdateAvatarRequest.builder()
                .avatarUrl("https://example.com/new_avatar.png")
                .build();

        ProfileResponse response = ProfileResponse.builder()
                .userId(sampleUserId)
                .avatarUrl("https://example.com/new_avatar.png")
                .build();

        when(userService.updateAvatar(any(), any(UpdateAvatarRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/users/avatar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.avatarUrl").value("https://example.com/new_avatar.png"));
    }
}
