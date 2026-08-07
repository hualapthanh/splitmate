package com.pm.userservice.service;

import com.pm.userservice.dto.request.UpdateAvatarRequest;
import com.pm.userservice.dto.request.UpdateProfileRequest;
import com.pm.userservice.dto.response.ProfileResponse;
import com.pm.userservice.dto.response.PublicProfileResponse;
import com.pm.userservice.entity.Profile;
import com.pm.userservice.exception.BusinessException;
import com.pm.userservice.exception.ResourceNotFoundException;
import com.pm.userservice.mapper.UserMapper;
import com.pm.userservice.repository.ProfileRepository;
import com.pm.userservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private Profile sampleProfile;
    private UUID sampleUserId;

    @BeforeEach
    void setUp() {
        sampleUserId = UUID.randomUUID();
        sampleProfile = Profile.builder()
                .userId(sampleUserId)
                .fullName("John Doe")
                .avatarUrl("https://example.com/john.jpg")
                .phoneNumber("+1234567890")
                .bio("Software Engineer")
                .timezone("UTC")
                .locale("en")
                .build();
    }

    @Test
    @DisplayName("getCurrentUserProfile() should return ProfileResponse when profile exists")
    void getCurrentUserProfile_success() {
        ProfileResponse expectedResponse = ProfileResponse.builder()
                .userId(sampleUserId)
                .fullName("John Doe")
                .build();

        when(profileRepository.findById(sampleUserId)).thenReturn(Optional.of(sampleProfile));
        when(userMapper.toProfileResponse(sampleProfile)).thenReturn(expectedResponse);

        ProfileResponse response = userService.getCurrentUserProfile(sampleUserId);

        assertNotNull(response);
        assertEquals(sampleUserId, response.getUserId());
        assertEquals("John Doe", response.getFullName());
    }

    @Test
    @DisplayName("getCurrentUserProfile() should throw ResourceNotFoundException when profile missing")
    void getCurrentUserProfile_notFound_throwsException() {
        when(profileRepository.findById(sampleUserId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getCurrentUserProfile(sampleUserId));
    }

    @Test
    @DisplayName("updateProfile() should update profile and return response")
    void updateProfile_success() {
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .fullName("John Smith")
                .timezone("Asia/Ho_Chi_Minh")
                .build();

        ProfileResponse expectedResponse = ProfileResponse.builder()
                .userId(sampleUserId)
                .fullName("John Smith")
                .timezone("Asia/Ho_Chi_Minh")
                .build();

        when(profileRepository.findById(sampleUserId)).thenReturn(Optional.of(sampleProfile));
        when(profileRepository.save(any(Profile.class))).thenReturn(sampleProfile);
        when(userMapper.toProfileResponse(any(Profile.class))).thenReturn(expectedResponse);

        ProfileResponse response = userService.updateProfile(sampleUserId, request);

        assertNotNull(response);
        assertEquals("John Smith", response.getFullName());
        verify(profileRepository).save(sampleProfile);
    }

    @Test
    @DisplayName("updateProfile() should throw BusinessException on invalid timezone")
    void updateProfile_invalidTimezone_throwsException() {
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .timezone("Invalid/Timezone_Name")
                .build();

        when(profileRepository.findById(sampleUserId)).thenReturn(Optional.of(sampleProfile));

        assertThrows(BusinessException.class, () -> userService.updateProfile(sampleUserId, request));
    }

    @Test
    @DisplayName("updateAvatar() should update avatarUrl and save")
    void updateAvatar_success() {
        UpdateAvatarRequest request = UpdateAvatarRequest.builder()
                .avatarUrl("https://example.com/new_avatar.png")
                .build();

        ProfileResponse expectedResponse = ProfileResponse.builder()
                .userId(sampleUserId)
                .avatarUrl("https://example.com/new_avatar.png")
                .build();

        when(profileRepository.findById(sampleUserId)).thenReturn(Optional.of(sampleProfile));
        when(profileRepository.save(any(Profile.class))).thenReturn(sampleProfile);
        when(userMapper.toProfileResponse(any(Profile.class))).thenReturn(expectedResponse);

        ProfileResponse response = userService.updateAvatar(sampleUserId, request);

        assertNotNull(response);
        assertEquals("https://example.com/new_avatar.png", response.getAvatarUrl());
    }

    @Test
    @DisplayName("getPublicProfile() should return PublicProfileResponse")
    void getPublicProfile_success() {
        PublicProfileResponse expectedResponse = PublicProfileResponse.builder()
                .userId(sampleUserId)
                .fullName("John Doe")
                .avatarUrl("https://example.com/john.jpg")
                .bio("Software Engineer")
                .build();

        when(profileRepository.findById(sampleUserId)).thenReturn(Optional.of(sampleProfile));
        when(userMapper.toPublicProfileResponse(sampleProfile)).thenReturn(expectedResponse);

        PublicProfileResponse response = userService.getPublicProfile(sampleUserId);

        assertNotNull(response);
        assertEquals("John Doe", response.getFullName());
    }
}
