package com.pm.groupservice.event;

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
public class MemberJoinedEvent {
    private UUID groupId;
    private UUID userId;
    private String role;
    private OffsetDateTime joinedAt;
}
