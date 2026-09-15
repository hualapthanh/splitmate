package com.pm.analyticsservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsOverviewResponse {
    private String currentPeriodMonth;
    private BigDecimal currentMonthTotalSpent;
    private int currentMonthTransactions;
    private String previousPeriodMonth;
    private BigDecimal previousMonthTotalSpent;
    private double percentageChange; // e.g. +15.5% or -10.2%
    private String topSpendingCategory;
    private BigDecimal topSpendingCategoryAmount;
}
