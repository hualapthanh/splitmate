package com.pm.expenseservice.event;

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
public class ExpenseUpdatedEvent {
    private UUID expenseId;
    private UUID userId;
    private UUID groupId;
    private String description;
    private BigDecimal amount;
    private LocalDate date;
    private List<ExpenseCreatedEvent.PayerItem> payers;
    private List<ExpenseCreatedEvent.SplitItem> splits;
    private OffsetDateTime updatedAt;
}
