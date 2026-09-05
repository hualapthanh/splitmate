package com.pm.budgetservice.dto.response;

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
public class BudgetResponse {
    private UUID id;
    private UUID userId;
    private UUID groupId;
    private BudgetScope scope;
    private String category;
    private BigDecimal amountLimit;
    private BigDecimal currentSpent;
    private BigDecimal remainingAmount;
    private double percentageUsed;
    private String status; // NORMAL, WARNING_80, WARNING_90, EXCEEDED
    private String periodMonth;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
