package com.pm.analyticsservice.event.listener;

import com.pm.analyticsservice.event.ExpenseCreatedEvent;
import com.pm.analyticsservice.event.SettlementCreatedEvent;
import com.pm.analyticsservice.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyticsEventListener {

    private final AnalyticsService analyticsService;

    @KafkaListener(topics = "${kafka.topics.expense-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleExpenseCreatedEvent(ExpenseCreatedEvent event) {
        log.info("AnalyticsService received ExpenseCreatedEvent for expenseId: {}", event.getExpenseId());
        try {
            analyticsService.processExpenseCreatedEvent(event);
        } catch (Exception e) {
            log.error("Failed to process ExpenseCreatedEvent in AnalyticsService", e);
        }
    }

    @KafkaListener(topics = "${kafka.topics.settlement-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleSettlementCreatedEvent(SettlementCreatedEvent event) {
        log.info("AnalyticsService received SettlementCreatedEvent for settlementId: {}", event.getSettlementId());
        try {
            analyticsService.processSettlementCreatedEvent(event);
        } catch (Exception e) {
            log.error("Failed to process SettlementCreatedEvent in AnalyticsService", e);
        }
    }
}
