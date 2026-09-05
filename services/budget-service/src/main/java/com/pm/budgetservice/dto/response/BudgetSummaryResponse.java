package com.pm.budgetservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetSummaryResponse {
    private String periodMonth;
    private int totalBudgets;
    private BigDecimal totalLimitAmount;
    private BigDecimal totalSpentAmount;
    private double overallPercentageUsed;
    private List<BudgetResponse> budgets;
}
