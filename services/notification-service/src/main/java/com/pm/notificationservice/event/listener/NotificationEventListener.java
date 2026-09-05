package com.pm.notificationservice.event.listener;

import com.pm.notificationservice.event.ExpenseCreatedEvent;
import com.pm.notificationservice.event.MemberJoinedEvent;
import com.pm.notificationservice.event.SettlementCreatedEvent;
import com.pm.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "${kafka.topics.group-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleMemberJoinedEvent(MemberJoinedEvent event) {
        log.info("NotificationService received MemberJoinedEvent for userId: {} in group: {}", event.getUserId(), event.getGroupId());
        try {
            notificationService.createAndSendNotification(
                    event.getUserId(),
                    null,
                    "Welcome to Group!",
                    "You have successfully joined the group",
                    "MEMBER_JOINED",
                    event.getGroupId()
            );
        } catch (Exception e) {
            log.error("Failed to process MemberJoinedEvent", e);
        }
    }

    @KafkaListener(topics = "${kafka.topics.expense-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleExpenseCreatedEvent(ExpenseCreatedEvent event) {
        log.info("NotificationService received ExpenseCreatedEvent for expenseId: {}", event.getExpenseId());
        try {
            if (event.getSplits() != null) {
                for (ExpenseCreatedEvent.SplitItem split : event.getSplits()) {
                    if (!split.getUserId().equals(event.getUserId())) {
                        String title = "New Expense Added";
                        String content = String.format("An expense '%s' of %s VND was added", event.getDescription(), event.getAmount());

                        notificationService.createAndSendNotification(
                                split.getUserId(),
                                event.getUserId(),
                                title,
                                content,
                                "EXPENSE_CREATED",
                                event.getExpenseId()
                        );
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to process ExpenseCreatedEvent", e);
        }
    }

    @KafkaListener(topics = "${kafka.topics.settlement-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleSettlementCreatedEvent(SettlementCreatedEvent event) {
        log.info("NotificationService received SettlementCreatedEvent for settlementId: {}", event.getSettlementId());
        try {
            String title = "Payment Received";
            String content = String.format("A payment of %s %s has been recorded for you", event.getAmount(), event.getCurrency());

            notificationService.createAndSendNotification(
                    event.getPayeeId(),
                    event.getPayerId(),
                    title,
                    content,
                    "SETTLEMENT_CREATED",
                    event.getSettlementId()
            );
        } catch (Exception e) {
            log.error("Failed to process SettlementCreatedEvent", e);
        }
    }

    @KafkaListener(topics = "${kafka.topics.budget-events:budget-events-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleBudgetAlertEvent(com.pm.notificationservice.event.BudgetAlertEvent event) {
        log.info("NotificationService received BudgetAlertEvent for budgetId: {} (threshold: {}%)", event.getBudgetId(), event.getThresholdPercent());
        try {
            if (event.getUserId() != null) {
                String title = String.format("Budget Alert (%d%%)", event.getThresholdPercent());
                notificationService.createAndSendNotification(
                        event.getUserId(),
                        null,
                        title,
                        event.getMessage(),
                        "BUDGET_ALERT",
                        event.getBudgetId()
                );
            }
        } catch (Exception e) {
            log.error("Failed to process BudgetAlertEvent", e);
        }
    }
}

