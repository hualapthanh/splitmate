package com.pm.groupservice.controller;

import com.pm.groupservice.dto.request.AddMemberRequest;
import com.pm.groupservice.dto.request.CreateGroupRequest;
import com.pm.groupservice.dto.request.JoinGroupRequest;
import com.pm.groupservice.dto.request.UpdateGroupRequest;
import com.pm.groupservice.dto.request.UpdateMemberRoleRequest;
import com.pm.groupservice.dto.response.GroupDetailResponse;
import com.pm.groupservice.dto.response.GroupMemberResponse;
import com.pm.groupservice.dto.response.GroupResponse;
import com.pm.groupservice.security.UserPrincipal;
import com.pm.groupservice.security.annotation.CurrentUser;
import com.pm.groupservice.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
@Tag(name = "Group Management", description = "Endpoints for group creation, member management, invite codes, and group details")
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new group", description = "Creates a new group. Creator is automatically set as Owner and Admin.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Group created successfully"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public GroupResponse createGroup(
            @CurrentUser UserPrincipal principal,
            @Valid @RequestBody CreateGroupRequest request
    ) {
        return groupService.createGroup(principal.getUserId(), request);
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get user's active groups", description = "Returns list of all active groups the authenticated user belongs to.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Groups retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public List<GroupResponse> getUserGroups(@CurrentUser UserPrincipal principal) {
        return groupService.getUserGroups(principal.getUserId());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get group details", description = "Returns details and active member list of a group. User must be a group member.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Group details retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied. User is not a group member"),
        @ApiResponse(responseCode = "404", description = "Group not found")
    })
    public GroupDetailResponse getGroupDetails(
            @CurrentUser UserPrincipal principal,
            @PathVariable("id") UUID groupId
    ) {
        return groupService.getGroupDetails(principal.getUserId(), groupId);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update group information", description = "Updates group name, description, type, or currency. Requires Group Admin role.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Group updated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied. Admin permission required"),
        @ApiResponse(responseCode = "404", description = "Group not found")
    })
    public GroupResponse updateGroup(
            @CurrentUser UserPrincipal principal,
            @PathVariable("id") UUID groupId,
            @Valid @RequestBody UpdateGroupRequest request
    ) {
        return groupService.updateGroup(principal.getUserId(), groupId, request);
    }

    @PostMapping("/{id}/invite-code")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Generate new group invite code", description = "Generates a new 8-character invite code. Requires Group Admin role.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Invite code generated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied. Admin permission required")
    })
    public GroupResponse generateInviteCode(
            @CurrentUser UserPrincipal principal,
            @PathVariable("id") UUID groupId
    ) {
        return groupService.generateInviteCode(principal.getUserId(), groupId);
    }

    @PostMapping("/join")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Join group via invite code", description = "Joins a group using a valid invite code. Joins as regular MEMBER.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Joined group successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid or expired invite code")
    })
    public GroupResponse joinGroup(
            @CurrentUser UserPrincipal principal,
            @Valid @RequestBody JoinGroupRequest request
    ) {
        return groupService.joinGroup(principal.getUserId(), request);
    }

    @PostMapping("/{id}/members")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add member to group", description = "Adds a user to the group by User ID. Requires Group Admin role.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Member added successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied. Admin permission required"),
        @ApiResponse(responseCode = "409", description = "User is already a member")
    })
    public GroupMemberResponse addMember(
            @CurrentUser UserPrincipal principal,
            @PathVariable("id") UUID groupId,
            @Valid @RequestBody AddMemberRequest request
    ) {
        return groupService.addMember(principal.getUserId(), groupId, request);
    }

    @DeleteMapping("/{id}/members/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove member or leave group", description = "Removes a member from the group (Admin only) or leaves the group (Self).")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Member removed or left group successfully"),
        @ApiResponse(responseCode = "400", description = "Cannot remove owner or last Admin"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public void removeMember(
            @CurrentUser UserPrincipal principal,
            @PathVariable("id") UUID groupId,
            @PathVariable("userId") UUID targetUserId
    ) {
        groupService.removeMember(principal.getUserId(), groupId, targetUserId);
    }

    @PutMapping("/{id}/members/{userId}/role")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update member role", description = "Updates a member's role (ADMIN or MEMBER). Requires Group Admin role.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Role updated successfully"),
        @ApiResponse(responseCode = "400", description = "Cannot demote owner or last Admin"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public GroupMemberResponse updateMemberRole(
            @CurrentUser UserPrincipal principal,
            @PathVariable("id") UUID groupId,
            @PathVariable("userId") UUID targetUserId,
            @Valid @RequestBody UpdateMemberRoleRequest request
    ) {
        return groupService.updateMemberRole(principal.getUserId(), groupId, targetUserId, request);
    }
}
