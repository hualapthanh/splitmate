package com.pm.budgetservice.mapper;

import com.pm.budgetservice.dto.request.CreateBudgetRequest;
import com.pm.budgetservice.dto.response.BudgetResponse;
import com.pm.budgetservice.entity.Budget;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper(componentModel = "spring")
public interface BudgetMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "currentSpent", expression = "java(java.math.BigDecimal.ZERO)")
    @Mapping(target = "alert80Sent", constant = "false")
    @Mapping(target = "alert90Sent", constant = "false")
    @Mapping(target = "alert100Sent", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Budget toEntity(CreateBudgetRequest request);

    @Mapping(target = "remainingAmount", expression = "java(calculateRemaining(budget))")
    @Mapping(target = "percentageUsed", expression = "java(calculatePercentage(budget))")
    @Mapping(target = "status", expression = "java(determineStatus(budget))")
    BudgetResponse toResponse(Budget budget);

    default BigDecimal calculateRemaining(Budget budget) {
        if (budget == null || budget.getAmountLimit() == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal spent = budget.getCurrentSpent() != null ? budget.getCurrentSpent() : BigDecimal.ZERO;
        BigDecimal remaining = budget.getAmountLimit().subtract(spent);
        return remaining.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : remaining;
    }

    default double calculatePercentage(Budget budget) {
        if (budget == null || budget.getAmountLimit() == null || budget.getAmountLimit().compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        BigDecimal spent = budget.getCurrentSpent() != null ? budget.getCurrentSpent() : BigDecimal.ZERO;
        return spent.multiply(BigDecimal.valueOf(100))
                .divide(budget.getAmountLimit(), 2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    default String determineStatus(Budget budget) {
        double pct = calculatePercentage(budget);
        if (pct >= 100.0) {
            return "EXCEEDED";
        } else if (pct >= 90.0) {
            return "WARNING_90";
        } else if (pct >= 80.0) {
            return "WARNING_80";
        } else {
            return "NORMAL";
        }
    }
}
