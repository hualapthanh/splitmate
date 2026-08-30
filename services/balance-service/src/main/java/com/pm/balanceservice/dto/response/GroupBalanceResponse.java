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
@Schema(description = "Group user net balance response payload")
public class GroupBalanceResponse {

    @Schema(example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d", description = "Balance ID")
    private UUID id;

    @Schema(example = "8f1d8b12-3456-7890-abcd-ef1234567890", description = "Group ID")
    private UUID groupId;

    @Schema(example = "9f8e7d6c-5b4a-3f2e-1d0c-9b8a7f6e5d4c", description = "User ID")
    private UUID userId;

    @Schema(example = "150000.00", description = "Net balance amount. Positive = owed money, Negative = owes money, 0 = balanced")
    private BigDecimal netBalance;
}
