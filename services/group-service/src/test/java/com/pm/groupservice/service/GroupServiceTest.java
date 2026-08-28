package com.pm.groupservice.service;

import com.pm.groupservice.dto.request.CreateGroupRequest;
import com.pm.groupservice.dto.request.JoinGroupRequest;
import com.pm.groupservice.dto.response.GroupDetailResponse;
import com.pm.groupservice.dto.response.GroupResponse;
import com.pm.groupservice.entity.Group;
import com.pm.groupservice.entity.GroupMember;
import com.pm.groupservice.event.GroupEventPublisher;
import com.pm.groupservice.exception.BusinessException;
import com.pm.groupservice.exception.ResourceNotFoundException;
import com.pm.groupservice.mapper.GroupMapper;
import com.pm.groupservice.repository.GroupMemberRepository;
import com.pm.groupservice.repository.GroupRepository;
import com.pm.groupservice.service.impl.GroupServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupMemberRepository groupMemberRepository;

    @Mock
    private GroupMapper groupMapper;

    @Mock
    private GroupEventPublisher groupEventPublisher;

    @InjectMocks
    private GroupServiceImpl groupService;

    private UUID sampleUserId;
    private UUID sampleGroupId;
    private Group sampleGroup;

    @BeforeEach
    void setUp() {
        sampleUserId = UUID.randomUUID();
        sampleGroupId = UUID.randomUUID();
        sampleGroup = Group.builder()
                .id(sampleGroupId)
                .name("Summer Trip 2026")
                .description("Beach trip")
                .ownerId(sampleUserId)
                .groupType("TRIP")
                .currency("VND")
                .inviteCode("SM-123456")
                .status("ACTIVE")
                .members(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("createGroup() should save group, add owner as ADMIN, and publish Kafka event")
    void createGroup_success() {
        CreateGroupRequest request = CreateGroupRequest.builder()
                .name("Summer Trip 2026")
                .description("Beach trip")
                .groupType("TRIP")
                .currency("VND")
                .build();

        GroupResponse expectedResponse = GroupResponse.builder()
                .id(sampleGroupId)
                .name("Summer Trip 2026")
                .ownerId(sampleUserId)
                .inviteCode("SM-123456")
                .build();

        when(groupMapper.toGroup(request)).thenReturn(sampleGroup);
        when(groupRepository.save(any(Group.class))).thenReturn(sampleGroup);
        when(groupMapper.toGroupResponse(sampleGroup)).thenReturn(expectedResponse);

        GroupResponse response = groupService.createGroup(sampleUserId, request);

        assertNotNull(response);
        assertEquals("Summer Trip 2026", response.getName());
        verify(groupRepository).save(sampleGroup);
        verify(groupEventPublisher).publishGroupCreatedEvent(any());
    }

    @Test
    @DisplayName("getGroupDetails() should return details when user is active member")
    void getGroupDetails_success() {
        GroupDetailResponse expectedResponse = GroupDetailResponse.builder()
                .id(sampleGroupId)
                .name("Summer Trip 2026")
                .members(new ArrayList<>())
                .build();

        when(groupRepository.findById(sampleGroupId)).thenReturn(Optional.of(sampleGroup));
        when(groupMemberRepository.existsByGroupIdAndUserIdAndStatus(sampleGroupId, sampleUserId, "ACTIVE")).thenReturn(true);
        when(groupMapper.toGroupDetailResponse(sampleGroup)).thenReturn(expectedResponse);

        GroupDetailResponse response = groupService.getGroupDetails(sampleUserId, sampleGroupId);

        assertNotNull(response);
        assertEquals("Summer Trip 2026", response.getName());
    }

    @Test
    @DisplayName("getGroupDetails() should throw BusinessException when user is not group member")
    void getGroupDetails_notMember_throwsException() {
        when(groupRepository.findById(sampleGroupId)).thenReturn(Optional.of(sampleGroup));
        when(groupMemberRepository.existsByGroupIdAndUserIdAndStatus(sampleGroupId, sampleUserId, "ACTIVE")).thenReturn(false);

        assertThrows(BusinessException.class, () -> groupService.getGroupDetails(sampleUserId, sampleGroupId));
    }

    @Test
    @DisplayName("joinGroup() should add user as MEMBER and publish Kafka event")
    void joinGroup_success() {
        JoinGroupRequest request = JoinGroupRequest.builder()
                .inviteCode("SM-123456")
                .build();

        GroupResponse expectedResponse = GroupResponse.builder()
                .id(sampleGroupId)
                .name("Summer Trip 2026")
                .build();

        when(groupRepository.findByInviteCode("SM-123456")).thenReturn(Optional.of(sampleGroup));
        when(groupMemberRepository.findByGroupIdAndUserId(sampleGroupId, sampleUserId)).thenReturn(Optional.empty());
        when(groupMapper.toGroupResponse(sampleGroup)).thenReturn(expectedResponse);

        GroupResponse response = groupService.joinGroup(sampleUserId, request);

        assertNotNull(response);
        verify(groupMemberRepository).save(any(GroupMember.class));
        verify(groupEventPublisher).publishMemberJoinedEvent(any());
    }

    @Test
    @DisplayName("joinGroup() should throw BusinessException on invalid invite code")
    void joinGroup_invalidInviteCode_throwsException() {
        JoinGroupRequest request = JoinGroupRequest.builder()
                .inviteCode("INVALID")
                .build();

        when(groupRepository.findByInviteCode("INVALID")).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> groupService.joinGroup(sampleUserId, request));
    }
}
