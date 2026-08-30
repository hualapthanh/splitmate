package com.pm.balanceservice.event.listener;

import com.pm.balanceservice.event.ExpenseCreatedEvent;
import com.pm.balanceservice.service.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExpenseEventListener {

    private final BalanceService balanceService;

    @KafkaListener(topics = "${kafka.topics.expense-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleExpenseCreatedEvent(ExpenseCreatedEvent event) {
        log.info("Received ExpenseCreatedEvent via Kafka for expenseId: {}", event.getExpenseId());
        try {
            balanceService.processExpenseCreatedEvent(event);
        } catch (Exception e) {
            log.error("Failed to process ExpenseCreatedEvent for expenseId: {}", event.getExpenseId(), e);
        }
    }
}
