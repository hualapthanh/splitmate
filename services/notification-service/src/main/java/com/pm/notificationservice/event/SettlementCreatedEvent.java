package com.pm.notificationservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementCreatedEvent {
    private UUID settlementId;
    private UUID groupId;
    private UUID payerId;
    private UUID payeeId;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private OffsetDateTime settledAt;
}
