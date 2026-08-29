package com.pm.expenseservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Split response payload")
public class SplitResponse {

    @Schema(example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d", description = "Split record ID")
    private UUID id;

    @Schema(example = "8f1d8b12-3456-7890-abcd-ef1234567890", description = "Expense ID")
    private UUID expenseId;

    @Schema(example = "9f8e7d6c-5b4a-3f2e-1d0c-9b8a7f6e5d4c", description = "User ID who owes")
    private UUID userId;

    @Schema(example = "EQUAL", description = "Split calculation type")
    private String splitType;

    @Schema(example = "200000.00", description = "Calculated split amount owed by this user")
    private BigDecimal amount;

    @Schema(example = "33.33", description = "Percentage share")
    private BigDecimal percentage;

    @Schema(example = "1.00", description = "Weighted shares")
    private BigDecimal shares;
}
