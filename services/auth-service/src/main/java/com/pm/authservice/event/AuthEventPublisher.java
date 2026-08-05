package com.pm.authservice.event;

import com.pm.authservice.config.KafkaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaProperties kafkaProperties;

    public void publishUserRegistered(UUID accountId, String email) {
        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .accountId(accountId)
                .email(email)
                .registeredAt(OffsetDateTime.now())
                .build();

        String topic = kafkaProperties.getTopic().getUserRegistered();
        log.info("Publishing UserRegisteredEvent to topic {}: {}", topic, event);
        kafkaTemplate.send(topic, accountId.toString(), event);
    }

    public void publishPasswordChanged(UUID accountId) {
        PasswordChangedEvent event = PasswordChangedEvent.builder()
                .accountId(accountId)
                .changedAt(OffsetDateTime.now())
                .build();

        String topic = kafkaProperties.getTopic().getPasswordChanged();
        log.info("Publishing PasswordChangedEvent to topic {}: {}", topic, event);
        kafkaTemplate.send(topic, accountId.toString(), event);
    }
}
