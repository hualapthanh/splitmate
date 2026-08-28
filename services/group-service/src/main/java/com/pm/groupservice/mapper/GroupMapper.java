package com.pm.groupservice.mapper;

import com.pm.groupservice.dto.request.CreateGroupRequest;
import com.pm.groupservice.dto.request.UpdateGroupRequest;
import com.pm.groupservice.dto.response.GroupDetailResponse;
import com.pm.groupservice.dto.response.GroupMemberResponse;
import com.pm.groupservice.dto.response.GroupResponse;
import com.pm.groupservice.entity.Group;
import com.pm.groupservice.entity.GroupMember;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GroupMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "inviteCode", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "privacyLevel", ignore = true)
    @Mapping(target = "members", ignore = true)
    Group toGroup(CreateGroupRequest request);

    @Mapping(target = "memberCount", expression = "java(group.getMembers() != null ? (int) group.getMembers().stream().filter(m -> \"ACTIVE\".equals(m.getStatus())).count() : 0)")
    GroupResponse toGroupResponse(Group group);

    GroupDetailResponse toGroupDetailResponse(Group group);

    @Mapping(target = "groupId", source = "group.id")
    GroupMemberResponse toGroupMemberResponse(GroupMember member);

    List<GroupMemberResponse> toGroupMemberResponseList(List<GroupMember> members);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateGroupFromRequest(UpdateGroupRequest request, @MappingTarget Group group);
}
