package com.pm.userservice.service.impl;

import com.pm.userservice.dto.request.UpdateAvatarRequest;
import com.pm.userservice.dto.request.UpdateProfileRequest;
import com.pm.userservice.dto.response.ProfileResponse;
import com.pm.userservice.dto.response.PublicProfileResponse;
import com.pm.userservice.entity.Profile;
import com.pm.userservice.exception.BusinessException;
import com.pm.userservice.exception.ErrorCode;
import com.pm.userservice.exception.ResourceNotFoundException;
import com.pm.userservice.mapper.UserMapper;
import com.pm.userservice.repository.ProfileRepository;
import com.pm.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.ZoneId;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final ProfileRepository profileRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getCurrentUserProfile(UUID userId) {
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for userId: " + userId));
        return userMapper.toProfileResponse(profile);
    }

    @Override
    @Transactional
    public ProfileResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for userId: " + userId));

        // Validate timezone if provided
        if (StringUtils.hasText(request.getTimezone())) {
            try {
                ZoneId.of(request.getTimezone());
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.USER_004, "Invalid timezone identifier: " + request.getTimezone());
            }
        }

        // Sanitize bio (strip HTML tags) & trim fullName
        if (request.getFullName() != null) {
            request.setFullName(request.getFullName().trim());
        }
        if (request.getBio() != null) {
            request.setBio(request.getBio().replaceAll("<[^>]*>", "").trim());
        }

        userMapper.updateProfileFromRequest(request, profile);
        Profile updatedProfile = profileRepository.save(profile);

        log.info("Audit Log: Profile updated successfully for userId: {}", userId);
        return userMapper.toProfileResponse(updatedProfile);
    }

    @Override
    @Transactional
    public ProfileResponse updateAvatar(UUID userId, UpdateAvatarRequest request) {
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for userId: " + userId));

        profile.setAvatarUrl(request.getAvatarUrl().trim());
        Profile updatedProfile = profileRepository.save(profile);

        log.info("Audit Log: Avatar updated successfully for userId: {}", userId);
        return userMapper.toProfileResponse(updatedProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public PublicProfileResponse getPublicProfile(UUID userId) {
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Public profile not found for userId: " + userId));
        return userMapper.toPublicProfileResponse(profile);
    }
}
