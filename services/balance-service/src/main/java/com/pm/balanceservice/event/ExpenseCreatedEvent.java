package com.pm.balanceservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseCreatedEvent {
    private UUID expenseId;
    private UUID userId;
    private UUID groupId;
    private String expenseType;
    private String description;
    private BigDecimal amount;
    private LocalDate date;
    private List<PayerItem> payers;
    private List<SplitItem> splits;
    private OffsetDateTime createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PayerItem {
        private UUID userId;
        private BigDecimal amountPaid;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SplitItem {
        private UUID userId;
        private String splitType;
        private BigDecimal amount;
    }
}
