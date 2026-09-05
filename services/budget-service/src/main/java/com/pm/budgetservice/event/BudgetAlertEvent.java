package com.pm.budgetservice.event;

import com.pm.budgetservice.entity.BudgetScope;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetAlertEvent {
    private UUID budgetId;
    private UUID userId;
    private UUID groupId;
    private BudgetScope scope;
    private String category;
    private int thresholdPercent; // 80, 90, 100
    private BigDecimal amountLimit;
    private BigDecimal currentSpent;
    private String periodMonth;
    private String message;
    private LocalDateTime timestamp;
}
