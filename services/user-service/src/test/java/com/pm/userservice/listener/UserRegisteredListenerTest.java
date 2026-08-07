package com.pm.userservice.listener;

import com.pm.userservice.entity.Profile;
import com.pm.userservice.repository.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRegisteredListenerTest {

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private UserRegisteredListener listener;

    private UUID sampleAccountId;

    @BeforeEach
    void setUp() {
        sampleAccountId = UUID.randomUUID();
    }

    @Test
    @DisplayName("handleUserRegistered() should create profile when account is new")
    void handleUserRegistered_newAccount_createsProfile() {
        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .accountId(sampleAccountId)
                .email("john.doe@example.com")
                .registeredAt(OffsetDateTime.now())
                .build();

        when(profileRepository.existsById(sampleAccountId)).thenReturn(false);

        listener.handleUserRegistered(event);

        verify(profileRepository).save(argThat(profile ->
                profile.getUserId().equals(sampleAccountId) &&
                "john.doe".equals(profile.getFullName()) &&
                "UTC".equals(profile.getTimezone()) &&
                "en".equals(profile.getLocale())
        ));
    }

    @Test
    @DisplayName("handleUserRegistered() should skip creation when profile already exists (idempotency)")
    void handleUserRegistered_existingProfile_skipsCreation() {
        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .accountId(sampleAccountId)
                .email("john.doe@example.com")
                .registeredAt(OffsetDateTime.now())
                .build();

        when(profileRepository.existsById(sampleAccountId)).thenReturn(true);

        listener.handleUserRegistered(event);

        verify(profileRepository, never()).save(any(Profile.class));
    }
}
