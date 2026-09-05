package com.pm.budgetservice.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBudgetRequest {

    @NotNull(message = "Amount limit is required")
    @DecimalMin(value = "1000.00", message = "Amount limit must be at least 1,000 VND")
    private BigDecimal amountLimit;

    private String category;
}
