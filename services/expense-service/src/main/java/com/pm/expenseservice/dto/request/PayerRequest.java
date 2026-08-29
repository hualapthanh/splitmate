package com.pm.expenseservice.dto.request;

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
@Schema(description = "Expense payer request payload")
public class PayerRequest {

    @NotNull(message = "Payer user ID is required")
    @Schema(example = "8f1d8b12-3456-7890-abcd-ef1234567890", description = "User ID who paid")
    private UUID userId;

    @NotNull(message = "Amount paid is required")
    @DecimalMin(value = "0.01", message = "Amount paid must be greater than zero")
    @Schema(example = "400000.00", description = "Amount paid by this user")
    private BigDecimal amountPaid;

    @Schema(example = "CASH", description = "Payment method (CASH, CREDIT_CARD, BANK_TRANSFER)")
    private String paymentMethod;
}
