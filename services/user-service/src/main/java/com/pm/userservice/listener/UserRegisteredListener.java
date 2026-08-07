package com.pm.userservice.listener;

import com.pm.userservice.entity.Profile;
import com.pm.userservice.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegisteredListener {

    private final ProfileRepository profileRepository;

    @KafkaListener(
            topics = "${kafka.topics.user-registered}",
            groupId = "${kafka.consumer-group}"
    )
    @Transactional
    public void handleUserRegistered(UserRegisteredEvent event) {
        log.info("Received UserRegisteredEvent from Kafka for accountId: {}, email: {}",
                event.getAccountId(), event.getEmail());

        // Idempotency check: Ignore if profile already exists
        if (profileRepository.existsById(event.getAccountId())) {
            log.warn("Profile already exists for accountId: {}. Skipping creation.", event.getAccountId());
            return;
        }

        String defaultFullName = event.getEmail() != null && event.getEmail().contains("@")
                ? event.getEmail().substring(0, event.getEmail().indexOf("@"))
                : "User";

        Profile profile = Profile.builder()
                .userId(event.getAccountId())
                .fullName(defaultFullName)
                .timezone("UTC")
                .locale("en")
                .build();

        profileRepository.save(profile);
        log.info("Successfully created default Profile for userId: {} (fullName: {})", profile.getUserId(), defaultFullName);
    }
}
