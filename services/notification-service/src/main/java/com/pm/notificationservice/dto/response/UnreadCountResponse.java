package com.pm.notificationservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Unread notifications count payload")
public class UnreadCountResponse {

    @Schema(example = "3", description = "Number of unread notifications for the user")
    private long unreadCount;
}
