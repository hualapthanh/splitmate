package com.pm.groupservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Join group using invite code request payload")
public class JoinGroupRequest {

    @NotBlank(message = "Invite code is required")
    @Size(max = 20, message = "Invite code cannot exceed 20 characters")
    @Schema(example = "SM-8X9Y7Z", description = "Group invite code")
    private String inviteCode;
}
