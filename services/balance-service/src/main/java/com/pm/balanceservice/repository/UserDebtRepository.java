package com.pm.balanceservice.repository;

import com.pm.balanceservice.entity.UserDebt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserDebtRepository extends JpaRepository<UserDebt, UUID> {
    List<UserDebt> findByGroupIdAndStatus(UUID groupId, String status);
    List<UserDebt> findByDebtorIdOrCreditorId(UUID debtorId, UUID creditorId);
    Optional<UserDebt> findByGroupIdAndDebtorIdAndCreditorId(UUID groupId, UUID debtorId, UUID creditorId);
}
