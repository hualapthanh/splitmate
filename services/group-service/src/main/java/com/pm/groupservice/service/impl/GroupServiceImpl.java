package com.pm.groupservice.service.impl;

import com.pm.groupservice.dto.request.AddMemberRequest;
import com.pm.groupservice.dto.request.CreateGroupRequest;
import com.pm.groupservice.dto.request.JoinGroupRequest;
import com.pm.groupservice.dto.request.UpdateGroupRequest;
import com.pm.groupservice.dto.request.UpdateMemberRoleRequest;
import com.pm.groupservice.dto.response.GroupDetailResponse;
import com.pm.groupservice.dto.response.GroupMemberResponse;
import com.pm.groupservice.dto.response.GroupResponse;
import com.pm.groupservice.entity.Group;
import com.pm.groupservice.entity.GroupMember;
import com.pm.groupservice.event.GroupCreatedEvent;
import com.pm.groupservice.event.GroupEventPublisher;
import com.pm.groupservice.event.MemberJoinedEvent;
import com.pm.groupservice.exception.BusinessException;
import com.pm.groupservice.exception.ErrorCode;
import com.pm.groupservice.exception.ResourceNotFoundException;
import com.pm.groupservice.mapper.GroupMapper;
import com.pm.groupservice.repository.GroupMemberRepository;
import com.pm.groupservice.repository.GroupRepository;
import com.pm.groupservice.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupMapper groupMapper;
    private final GroupEventPublisher groupEventPublisher;

    @Override
    @Transactional
    public GroupResponse createGroup(UUID userId, CreateGroupRequest request) {
        log.info("Creating new group '{}' by owner userId: {}", request.getName(), userId);

        Group group = groupMapper.toGroup(request);
        group.setOwnerId(userId);
        group.setStatus("ACTIVE");
        group.setInviteCode(generateUniqueInviteCode());

        // Add creator as first member with ADMIN role
        GroupMember ownerMember = GroupMember.builder()
                .group(group)
                .userId(userId)
                .role("ADMIN")
                .status("ACTIVE")
                .build();

        group.getMembers().add(ownerMember);

        Group savedGroup = groupRepository.save(group);

        // Publish Kafka Event
        GroupCreatedEvent createdEvent = GroupCreatedEvent.builder()
                .groupId(savedGroup.getId())
                .name(savedGroup.getName())
                .ownerId(savedGroup.getOwnerId())
                .currency(savedGroup.getCurrency())
                .createdAt(savedGroup.getCreatedAt())
                .build();
        groupEventPublisher.publishGroupCreatedEvent(createdEvent);

        log.info("Group created successfully with groupId: {} and inviteCode: {}", savedGroup.getId(), savedGroup.getInviteCode());
        return groupMapper.toGroupResponse(savedGroup);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupResponse> getUserGroups(UUID userId) {
        List<Group> userGroups = groupRepository.findAllByUserId(userId);
        return userGroups.stream()
                .map(groupMapper::toGroupResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GroupDetailResponse getGroupDetails(UUID userId, UUID groupId) {
        Group group = findGroupByIdOrThrow(groupId);
        verifyUserIsActiveMember(groupId, userId);

        List<GroupMember> activeMembers = groupMemberRepository.findByGroupIdAndStatus(groupId, "ACTIVE");
        GroupDetailResponse response = groupMapper.toGroupDetailResponse(group);
        response.setMembers(groupMapper.toGroupMemberResponseList(activeMembers));
        return response;
    }

    @Override
    @Transactional
    public GroupResponse updateGroup(UUID userId, UUID groupId, UpdateGroupRequest request) {
        Group group = findGroupByIdOrThrow(groupId);
        verifyUserIsAdmin(groupId, userId);

        groupMapper.updateGroupFromRequest(request, group);
        Group updatedGroup = groupRepository.save(group);

        log.info("Audit: Group updated for groupId: {} by userId: {}", groupId, userId);
        return groupMapper.toGroupResponse(updatedGroup);
    }

    @Override
    @Transactional
    public GroupResponse generateInviteCode(UUID userId, UUID groupId) {
        Group group = findGroupByIdOrThrow(groupId);
        verifyUserIsAdmin(groupId, userId);

        String newInviteCode = generateUniqueInviteCode();
        group.setInviteCode(newInviteCode);
        Group updatedGroup = groupRepository.save(group);

        log.info("Generated new invite code '{}' for groupId: {} by userId: {}", newInviteCode, groupId, userId);
        return groupMapper.toGroupResponse(updatedGroup);
    }

    @Override
    @Transactional
    public GroupResponse joinGroup(UUID userId, JoinGroupRequest request) {
        String code = request.getInviteCode().trim().toUpperCase();
        Group group = groupRepository.findByInviteCode(code)
                .orElseThrow(() -> new BusinessException(ErrorCode.GRP_002, "Invalid or expired invite code: " + code));

        if (!"ACTIVE".equals(group.getStatus())) {
            throw new BusinessException(ErrorCode.GRP_002, "Target group is inactive");
        }

        Optional<GroupMember> existingMemberOpt = groupMemberRepository.findByGroupIdAndUserId(group.getId(), userId);

        if (existingMemberOpt.isPresent()) {
            GroupMember member = existingMemberOpt.get();
            if ("ACTIVE".equals(member.getStatus())) {
                log.info("Idempotent check: User {} is already an active member of group {}", userId, group.getId());
                return groupMapper.toGroupResponse(group);
            } else {
                member.setStatus("ACTIVE");
                member.setJoinedAt(OffsetDateTime.now());
                groupMemberRepository.save(member);
            }
        } else {
            GroupMember newMember = GroupMember.builder()
                    .group(group)
                    .userId(userId)
                    .role("MEMBER")
                    .status("ACTIVE")
                    .build();
            groupMemberRepository.save(newMember);
        }

        // Publish MemberJoinedEvent to Kafka
        MemberJoinedEvent event = MemberJoinedEvent.builder()
                .groupId(group.getId())
                .userId(userId)
                .role("MEMBER")
                .joinedAt(OffsetDateTime.now())
                .build();
        groupEventPublisher.publishMemberJoinedEvent(event);

        log.info("User {} joined group {} via invite code", userId, group.getId());
        return groupMapper.toGroupResponse(group);
    }

    @Override
    @Transactional
    public GroupMemberResponse addMember(UUID currentUserId, UUID groupId, AddMemberRequest request) {
        Group group = findGroupByIdOrThrow(groupId);
        verifyUserIsAdmin(groupId, currentUserId);

        UUID targetUserId = request.getUserId();
        Optional<GroupMember> existingMemberOpt = groupMemberRepository.findByGroupIdAndUserId(groupId, targetUserId);

        GroupMember member;
        if (existingMemberOpt.isPresent()) {
            member = existingMemberOpt.get();
            if ("ACTIVE".equals(member.getStatus())) {
                throw new BusinessException(ErrorCode.GRP_004, "User is already an active member");
            }
            member.setStatus("ACTIVE");
            member.setJoinedAt(OffsetDateTime.now());
        } else {
            member = GroupMember.builder()
                    .group(group)
                    .userId(targetUserId)
                    .role("MEMBER")
                    .status("ACTIVE")
                    .build();
        }

        GroupMember savedMember = groupMemberRepository.save(member);
        log.info("User {} added target user {} to group {}", currentUserId, targetUserId, groupId);
        return groupMapper.toGroupMemberResponse(savedMember);
    }

    @Override
    @Transactional
    public void removeMember(UUID currentUserId, UUID groupId, UUID targetUserId) {
        Group group = findGroupByIdOrThrow(groupId);

        // Self leaving vs Admin removing another member
        boolean isSelf = currentUserId.equals(targetUserId);
        if (!isSelf) {
            verifyUserIsAdmin(groupId, currentUserId);
        }

        // Cannot remove group owner
        if (group.getOwnerId().equals(targetUserId)) {
            throw new BusinessException(ErrorCode.GRP_005, "Cannot remove group owner");
        }

        GroupMember targetMember = groupMemberRepository.findByGroupIdAndUserId(groupId, targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found in group"));

        if (!"ACTIVE".equals(targetMember.getStatus())) {
            return; // Already left/removed
        }

        // Check ADMIN count if target is ADMIN
        if ("ADMIN".equals(targetMember.getRole())) {
            long activeAdminCount = groupMemberRepository.countByGroupIdAndRoleAndStatus(groupId, "ADMIN", "ACTIVE");
            if (activeAdminCount <= 1) {
                throw new BusinessException(ErrorCode.GRP_006, "Group must have at least one active Admin");
            }
        }

        targetMember.setStatus(isSelf ? "LEFT" : "REMOVED");
        groupMemberRepository.save(targetMember);

        log.info("User {} {} group {}", targetUserId, isSelf ? "left" : "was removed from", groupId);
    }

    @Override
    @Transactional
    public GroupMemberResponse updateMemberRole(UUID currentUserId, UUID groupId, UUID targetUserId, UpdateMemberRoleRequest request) {
        Group group = findGroupByIdOrThrow(groupId);
        verifyUserIsAdmin(groupId, currentUserId);

        GroupMember targetMember = groupMemberRepository.findByGroupIdAndUserId(groupId, targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found in group"));

        String newRole = request.getRole().trim().toUpperCase();

        // Cannot demote group owner
        if (group.getOwnerId().equals(targetUserId) && "MEMBER".equals(newRole)) {
            throw new BusinessException(ErrorCode.GRP_005, "Group owner must retain ADMIN role");
        }

        // Demoting ADMIN to MEMBER requires at least one remaining ADMIN
        if ("ADMIN".equals(targetMember.getRole()) && "MEMBER".equals(newRole)) {
            long activeAdminCount = groupMemberRepository.countByGroupIdAndRoleAndStatus(groupId, "ADMIN", "ACTIVE");
            if (activeAdminCount <= 1) {
                throw new BusinessException(ErrorCode.GRP_006, "Group must have at least one active Admin");
            }
        }

        targetMember.setRole(newRole);
        GroupMember updatedMember = groupMemberRepository.save(targetMember);

        log.info("Role updated for member {} in group {} to {}", targetUserId, groupId, newRole);
        return groupMapper.toGroupMemberResponse(updatedMember);
    }

    private Group findGroupByIdOrThrow(UUID groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));
    }

    private void verifyUserIsActiveMember(UUID groupId, UUID userId) {
        boolean isMember = groupMemberRepository.existsByGroupIdAndUserIdAndStatus(groupId, userId, "ACTIVE");
        if (!isMember) {
            throw new BusinessException(ErrorCode.GRP_003, "Access denied. User is not an active member of this group");
        }
    }

    private void verifyUserIsAdmin(UUID groupId, UUID userId) {
        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GRP_003, "Access denied. User is not a member of this group"));

        if (!"ACTIVE".equals(member.getStatus()) || !"ADMIN".equals(member.getRole())) {
            throw new BusinessException(ErrorCode.GRP_003, "Access denied. Group Admin permission required");
        }
    }

    private String generateUniqueInviteCode() {
        return "SM-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
