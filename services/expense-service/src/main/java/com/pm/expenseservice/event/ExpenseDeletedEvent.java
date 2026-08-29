package com.pm.expenseservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseDeletedEvent {
    private UUID expenseId;
    private UUID userId;
    private UUID groupId;
    private OffsetDateTime deletedAt;
}
