package com.pm.balanceservice.dto.response;

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
@Schema(description = "Min-Cash-Flow simplified transaction response payload")
public class SimplifiedDebtResponse {

    @Schema(example = "9f8e7d6c-5b4a-3f2e-1d0c-9b8a7f6e5d4c", description = "Debtor user ID (who should pay)")
    private UUID debtorId;

    @Schema(example = "7f6e5d4c-3b2a-1f0e-9d8c-7b6a5f4e3d2c", description = "Creditor user ID (who should receive)")
    private UUID creditorId;

    @Schema(example = "100000.00", description = "Simplified settlement payment amount")
    private BigDecimal amount;
}
