package com.pm.notificationservice.dto.request;

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
@Schema(description = "Send payment reminder request payload")
public class SendReminderRequest {

    @NotNull(message = "Debtor user ID is required")
    @Schema(example = "8f1d8b12-3456-7890-abcd-ef1234567890", description = "User ID owing money")
    private UUID debtorId;

    @Schema(example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d", description = "Group ID context")
    private UUID groupId;

    @NotNull(message = "Debt amount is required")
    @DecimalMin(value = "0.01", message = "Reminder debt amount must be greater than zero")
    @Schema(example = "150000.00", description = "Debt amount owed")
    private BigDecimal amount;
}
