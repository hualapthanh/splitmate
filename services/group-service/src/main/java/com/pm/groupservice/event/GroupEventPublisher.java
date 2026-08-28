package com.pm.groupservice.event;

import com.pm.groupservice.config.KafkaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GroupEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaProperties kafkaProperties;

    public void publishGroupCreatedEvent(GroupCreatedEvent event) {
        String topic = kafkaProperties.getTopics().getGroupEvents();
        log.info("Publishing GroupCreatedEvent for groupId: {} to topic: {}", event.getGroupId(), topic);
        kafkaTemplate.send(topic, event.getGroupId().toString(), event);
    }

    public void publishMemberJoinedEvent(MemberJoinedEvent event) {
        String topic = kafkaProperties.getTopics().getGroupEvents();
        log.info("Publishing MemberJoinedEvent for userId: {} joined groupId: {} to topic: {}",
                event.getUserId(), event.getGroupId(), topic);
        kafkaTemplate.send(topic, event.getGroupId().toString(), event);
    }
}
