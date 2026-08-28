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
public class GroupCreatedEvent {
    private UUID groupId;
    private String name;
    private UUID ownerId;
    private String currency;
    private OffsetDateTime createdAt;
}
