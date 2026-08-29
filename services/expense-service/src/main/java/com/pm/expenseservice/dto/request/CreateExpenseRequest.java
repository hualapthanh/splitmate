package com.pm.expenseservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Create expense request payload")
public class CreateExpenseRequest {

    @Schema(example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d", description = "Group ID (NULL for personal expenses)")
    private UUID groupId;

    @NotBlank(message = "Expense type is required")
    @Pattern(regexp = "^(PERSONAL|GROUP)$", message = "Expense type must be PERSONAL or GROUP")
    @Schema(example = "GROUP", description = "Expense type (PERSONAL or GROUP)")
    private String expenseType;

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Schema(example = "Dinner at Seafood Restaurant", description = "Expense description")
    private String description;

    @NotNull(message = "Expense total amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @Schema(example = "600000.00", description = "Total expense amount")
    private BigDecimal amount;

    @NotNull(message = "Category ID is required")
    @Schema(example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d", description = "Category ID")
    private UUID categoryId;

    @NotNull(message = "Expense date is required")
    @Schema(example = "2026-08-28", description = "Date expense occurred")
    private LocalDate date;

    @NotBlank(message = "Split type is required")
    @Pattern(regexp = "^(EQUAL|EXACT|PERCENTAGE|SHARE)$", message = "Split type must be EQUAL, EXACT, PERCENTAGE, or SHARE")
    @Schema(example = "EQUAL", description = "Split calculation algorithm")
    private String splitType;

    @NotEmpty(message = "At least one payer is required")
    @Valid
    @Schema(description = "List of users who paid for this expense")
    private List<PayerRequest> payers;

    @NotEmpty(message = "At least one split participant is required")
    @Valid
    @Schema(description = "List of participants sharing this expense")
    private List<SplitRequest> splits;
}
