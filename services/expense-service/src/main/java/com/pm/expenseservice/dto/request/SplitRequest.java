package com.pm.expenseservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "Expense split request payload")
public class SplitRequest {

    @NotNull(message = "Participant user ID is required")
    @Schema(example = "8f1d8b12-3456-7890-abcd-ef1234567890", description = "User ID who owes/participates")
    private UUID userId;

    @Schema(example = "200000.00", description = "Exact split amount (used for EXACT split type)")
    private BigDecimal amount;

    @Schema(example = "50.00", description = "Percentage split value (used for PERCENTAGE split type)")
    private BigDecimal percentage;

    @Schema(example = "2.00", description = "Weighted shares value (used for SHARE split type)")
    private BigDecimal shares;
}
