package com.pm.budgetservice.event.listener;

import com.pm.budgetservice.event.ExpenseCreatedEvent;
import com.pm.budgetservice.service.BudgetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExpenseEventListener {

    private final BudgetService budgetService;

    @KafkaListener(topics = "${kafka.topics.expense-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleExpenseCreatedEvent(ExpenseCreatedEvent event) {
        log.info("BudgetService received ExpenseCreatedEvent via Kafka for expenseId: {}", event.getExpenseId());
        try {
            budgetService.processExpenseCreatedEvent(event);
        } catch (Exception e) {
            log.error("Failed to process ExpenseCreatedEvent for expenseId: {}", event.getExpenseId(), e);
        }
    }
}
