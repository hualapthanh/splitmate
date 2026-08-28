package com.pm.groupservice.dto.response;

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
@Schema(description = "Group member response payload")
public class GroupMemberResponse {

    @Schema(example = "8f1d8b12-3456-7890-abcd-ef1234567890", description = "Member unique identifier")
    private UUID id;

    @Schema(example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d", description = "Group identifier")
    private UUID groupId;

    @Schema(example = "9f8e7d6c-5b4a-3f2e-1d0c-9b8a7f6e5d4c", description = "User unique identifier")
    private UUID userId;

    @Schema(example = "ADMIN", description = "Member role (ADMIN or MEMBER)")
    private String role;

    @Schema(example = "ACTIVE", description = "Membership status")
    private String status;

    @Schema(example = "2026-08-27T10:00:00Z", description = "Joined timestamp")
    private OffsetDateTime joinedAt;
}
