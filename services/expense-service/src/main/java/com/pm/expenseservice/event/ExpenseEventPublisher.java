package com.pm.expenseservice.event;

import com.pm.expenseservice.config.KafkaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExpenseEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaProperties kafkaProperties;

    public void publishExpenseCreatedEvent(ExpenseCreatedEvent event) {
        String topic = kafkaProperties.getTopics().getExpenseEvents();
        log.info("Publishing ExpenseCreatedEvent for expenseId: {} to topic: {}", event.getExpenseId(), topic);
        kafkaTemplate.send(topic, event.getExpenseId().toString(), event);
    }

    public void publishExpenseUpdatedEvent(ExpenseUpdatedEvent event) {
        String topic = kafkaProperties.getTopics().getExpenseEvents();
        log.info("Publishing ExpenseUpdatedEvent for expenseId: {} to topic: {}", event.getExpenseId(), topic);
        kafkaTemplate.send(topic, event.getExpenseId().toString(), event);
    }

    public void publishExpenseDeletedEvent(ExpenseDeletedEvent event) {
        String topic = kafkaProperties.getTopics().getExpenseEvents();
        log.info("Publishing ExpenseDeletedEvent for expenseId: {} to topic: {}", event.getExpenseId(), topic);
        kafkaTemplate.send(topic, event.getExpenseId().toString(), event);
    }
}
