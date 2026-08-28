package com.pm.groupservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Group detail response payload including member list")
public class GroupDetailResponse {

    @Schema(example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d", description = "Group unique identifier")
    private UUID id;

    @Schema(example = "Summer Trip 2026", description = "Group name")
    private String name;

    @Schema(example = "Beach vacation expense tracking group", description = "Group description")
    private String description;

    @Schema(example = "9f8e7d6c-5b4a-3f2e-1d0c-9b8a7f6e5d4c", description = "Owner user ID")
    private UUID ownerId;

    @Schema(example = "TRIP", description = "Group type")
    private String groupType;

    @Schema(example = "VND", description = "Group currency ISO code")
    private String currency;

    @Schema(example = "PRIVATE", description = "Privacy level")
    private String privacyLevel;

    @Schema(example = "SM-8X9Y7Z", description = "Group invite code")
    private String inviteCode;

    @Schema(example = "ACTIVE", description = "Group status")
    private String status;

    @Schema(description = "List of active group members")
    private List<GroupMemberResponse> members;

    @Schema(example = "2026-08-27T10:00:00Z", description = "Created timestamp")
    private OffsetDateTime createdAt;

    @Schema(example = "2026-08-27T10:00:00Z", description = "Last updated timestamp")
    private OffsetDateTime updatedAt;
}
