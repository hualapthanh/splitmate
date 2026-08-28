package com.pm.groupservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Update member role request payload")
public class UpdateMemberRoleRequest {

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "^(ADMIN|MEMBER)$", message = "Role must be ADMIN or MEMBER")
    @Schema(example = "ADMIN", description = "New role (ADMIN or MEMBER)")
    private String role;
}
