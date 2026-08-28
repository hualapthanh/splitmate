package com.pm.groupservice.repository;

import com.pm.groupservice.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, UUID> {

    Optional<GroupMember> findByGroupIdAndUserId(UUID groupId, UUID userId);

    List<GroupMember> findByGroupIdAndStatus(UUID groupId, String status);

    boolean existsByGroupIdAndUserIdAndStatus(UUID groupId, UUID userId, String status);

    long countByGroupIdAndRoleAndStatus(UUID groupId, String role, String status);
}
