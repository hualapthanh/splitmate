package com.pm.balanceservice.repository;

import com.pm.balanceservice.entity.GroupBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupBalanceRepository extends JpaRepository<GroupBalance, UUID> {
    List<GroupBalance> findByGroupId(UUID groupId);
    List<GroupBalance> findByUserId(UUID userId);
    Optional<GroupBalance> findByGroupIdAndUserId(UUID groupId, UUID userId);
}
