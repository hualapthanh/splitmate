package com.pm.notificationservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Notification response payload")
public class NotificationResponse {

    @Schema(example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d", description = "Notification ID")
    private UUID id;

    @Schema(example = "8f1d8b12-3456-7890-abcd-ef1234567890", description = "Recipient user ID")
    private UUID recipientId;

    @Schema(example = "9f8e7d6c-5b4a-3f2e-1d0c-9b8a7f6e5d4c", description = "Sender user ID (NULL for system)")
    private UUID senderId;

    @Schema(example = "New Expense Added", description = "Notification title")
    private String title;

    @Schema(example = "User X added Seafood Dinner (600,000 VND)", description = "Notification message content")
    private String content;

    @Schema(example = "EXPENSE_CREATED", description = "Notification type")
    private String type;

    @Schema(example = "7f6e5d4c-3b2a-1f0e-9d8c-7b6a5f4e3d2c", description = "Reference entity ID (expense, group, or settlement)")
    private UUID referenceId;

    @Schema(example = "false", description = "Read status")
    private boolean isRead;

    @Schema(example = "2026-08-30T10:00:00Z", description = "Created timestamp")
    private OffsetDateTime createdAt;
}
