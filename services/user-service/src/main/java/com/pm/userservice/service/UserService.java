package com.pm.userservice.service;

import com.pm.userservice.dto.request.UpdateAvatarRequest;
import com.pm.userservice.dto.request.UpdateProfileRequest;
import com.pm.userservice.dto.response.ProfileResponse;
import com.pm.userservice.dto.response.PublicProfileResponse;

import java.util.UUID;

public interface UserService {
    ProfileResponse getCurrentUserProfile(UUID userId);
    ProfileResponse updateProfile(UUID userId, UpdateProfileRequest request);
    ProfileResponse updateAvatar(UUID userId, UpdateAvatarRequest request);
    PublicProfileResponse getPublicProfile(UUID userId);
}
