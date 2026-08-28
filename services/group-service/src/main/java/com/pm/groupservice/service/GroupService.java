package com.pm.groupservice.service;

import com.pm.groupservice.dto.request.AddMemberRequest;
import com.pm.groupservice.dto.request.CreateGroupRequest;
import com.pm.groupservice.dto.request.JoinGroupRequest;
import com.pm.groupservice.dto.request.UpdateGroupRequest;
import com.pm.groupservice.dto.request.UpdateMemberRoleRequest;
import com.pm.groupservice.dto.response.GroupDetailResponse;
import com.pm.groupservice.dto.response.GroupMemberResponse;
import com.pm.groupservice.dto.response.GroupResponse;

import java.util.List;
import java.util.UUID;

public interface GroupService {
    GroupResponse createGroup(UUID userId, CreateGroupRequest request);
    List<GroupResponse> getUserGroups(UUID userId);
    GroupDetailResponse getGroupDetails(UUID userId, UUID groupId);
    GroupResponse updateGroup(UUID userId, UUID groupId, UpdateGroupRequest request);
    GroupResponse generateInviteCode(UUID userId, UUID groupId);
    GroupResponse joinGroup(UUID userId, JoinGroupRequest request);
    GroupMemberResponse addMember(UUID currentUserId, UUID groupId, AddMemberRequest request);
    void removeMember(UUID currentUserId, UUID groupId, UUID targetUserId);
    GroupMemberResponse updateMemberRole(UUID currentUserId, UUID groupId, UUID targetUserId, UpdateMemberRoleRequest request);
}
