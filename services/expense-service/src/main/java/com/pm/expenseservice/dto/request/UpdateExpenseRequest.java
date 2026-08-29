package com.pm.expenseservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
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
@Schema(description = "Update expense request payload")
public class UpdateExpenseRequest {

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Schema(example = "Updated Dinner at Seafood Restaurant", description = "Updated description")
    private String description;

    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @Schema(example = "750000.00", description = "Updated total expense amount")
    private BigDecimal amount;

    @Schema(example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d", description = "Updated Category ID")
    private UUID categoryId;

    @Schema(example = "2026-08-28", description = "Updated date")
    private LocalDate date;

    @Pattern(regexp = "^(EQUAL|EXACT|PERCENTAGE|SHARE)$", message = "Split type must be EQUAL, EXACT, PERCENTAGE, or SHARE")
    @Schema(example = "EQUAL", description = "Updated split type algorithm")
    private String splitType;

    @Valid
    @Schema(description = "Updated payers list")
    private List<PayerRequest> payers;

    @Valid
    @Schema(description = "Updated splits list")
    private List<SplitRequest> splits;
}
