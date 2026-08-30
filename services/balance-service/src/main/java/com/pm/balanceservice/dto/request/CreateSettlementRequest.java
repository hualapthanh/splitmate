package com.pm.balanceservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
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
@Schema(description = "Create settlement payment request payload")
public class CreateSettlementRequest {

    @Schema(example = "8f1d8b12-3456-7890-abcd-ef1234567890", description = "Group ID (NULL for direct personal settlement)")
    private UUID groupId;

    @NotNull(message = "Payee user ID is required")
    @Schema(example = "7f6e5d4c-3b2a-1f0e-9d8c-7b6a5f4e3d2c", description = "User ID receiving the payment")
    private UUID payeeId;

    @NotNull(message = "Settlement amount is required")
    @DecimalMin(value = "0.01", message = "Settlement amount must be greater than zero")
    @Schema(example = "100000.00", description = "Amount paid")
    private BigDecimal amount;

    @Schema(example = "VND", description = "Currency code (default VND)")
    private String currency;

    @Schema(example = "BANK_TRANSFER", description = "Payment method (BANK_TRANSFER, CASH, MOMO, ZALOPAY)")
    private String paymentMethod;
}
