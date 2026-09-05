package com.pm.budgetservice.dto.request;

import com.pm.budgetservice.entity.BudgetScope;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBudgetRequest {

    @NotNull(message = "Scope is required (PERSONAL or GROUP)")
    private BudgetScope scope;

    private UUID groupId;

    @NotBlank(message = "Category is required (e.g. ALL, FOOD, TRANSPORT, ENTERTAINMENT)")
    private String category;

    @NotNull(message = "Amount limit is required")
    @DecimalMin(value = "1000.00", message = "Amount limit must be at least 1,000 VND")
    private BigDecimal amountLimit;

    @NotBlank(message = "Period month is required (YYYY-MM)")
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$", message = "Period month must be in format YYYY-MM")
    private String periodMonth;
}
