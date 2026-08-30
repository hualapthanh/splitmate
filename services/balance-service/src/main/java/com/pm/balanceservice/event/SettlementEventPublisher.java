package com.pm.balanceservice.event;

import com.pm.balanceservice.config.KafkaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SettlementEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaProperties kafkaProperties;

    public void publishSettlementCreatedEvent(SettlementCreatedEvent event) {
        String topic = kafkaProperties.getTopics().getSettlementEvents();
        log.info("Publishing SettlementCreatedEvent for settlementId: {} to topic: {}", event.getSettlementId(), topic);
        kafkaTemplate.send(topic, event.getSettlementId().toString(), event);
    }
}
