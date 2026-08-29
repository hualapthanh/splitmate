package com.pm.expenseservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Expense summary response payload")
public class ExpenseResponse {

    @Schema(example = "8f1d8b12-3456-7890-abcd-ef1234567890", description = "Expense unique identifier")
    private UUID id;

    @Schema(example = "9f8e7d6c-5b4a-3f2e-1d0c-9b8a7f6e5d4c", description = "Creator user ID")
    private UUID userId;

    @Schema(example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d", description = "Group ID (NULL for personal)")
    private UUID groupId;

    @Schema(example = "GROUP", description = "Expense type (PERSONAL or GROUP)")
    private String expenseType;

    @Schema(example = "Dinner at Seafood Restaurant", description = "Expense description")
    private String description;

    @Schema(example = "600000.00", description = "Total expense amount")
    private BigDecimal amount;

    @Schema(description = "Associated category details")
    private CategoryResponse category;

    @Schema(example = "2026-08-28", description = "Date expense occurred")
    private LocalDate date;

    @Schema(example = "CONFIRMED", description = "Expense status")
    private String status;

    @Schema(example = "2026-08-28T10:00:00Z", description = "Created timestamp")
    private OffsetDateTime createdAt;

    @Schema(example = "2026-08-28T10:00:00Z", description = "Last updated timestamp")
    private OffsetDateTime updatedAt;
}
