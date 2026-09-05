package com.pm.budgetservice.event;

import com.pm.budgetservice.config.KafkaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BudgetEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaProperties kafkaProperties;

    public void publishBudgetAlertEvent(BudgetAlertEvent event) {
        String topic = kafkaProperties.getTopics().getBudgetEvents();
        String key = event.getUserId() != null ? event.getUserId().toString() : event.getBudgetId().toString();

        log.info("Publishing BudgetAlertEvent to topic {}: userId={}, threshold={}%",
                topic, event.getUserId(), event.getThresholdPercent());

        kafkaTemplate.send(topic, key, event);
    }
}
