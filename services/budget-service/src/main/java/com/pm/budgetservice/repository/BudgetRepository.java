package com.pm.budgetservice.repository;

import com.pm.budgetservice.entity.Budget;
import com.pm.budgetservice.entity.BudgetScope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, UUID> {

    List<Budget> findByUserId(UUID userId);

    List<Budget> findByUserIdAndPeriodMonth(UUID userId, String periodMonth);

    List<Budget> findByGroupIdAndPeriodMonth(UUID groupId, String periodMonth);

    Optional<Budget> findByUserIdAndScopeAndGroupIdAndCategoryAndPeriodMonth(
            UUID userId, BudgetScope scope, UUID groupId, String category, String periodMonth
    );

    @Query("SELECT b FROM Budget b WHERE b.userId = :userId AND b.scope = 'PERSONAL' AND b.periodMonth = :periodMonth AND (b.category = :category OR b.category = 'ALL')")
    List<Budget> findMatchingPersonalBudgets(@Param("userId") UUID userId, @Param("category") String category, @Param("periodMonth") String periodMonth);

    @Query("SELECT b FROM Budget b WHERE b.groupId = :groupId AND b.scope = 'GROUP' AND b.periodMonth = :periodMonth AND (b.category = :category OR b.category = 'ALL')")
    List<Budget> findMatchingGroupBudgets(@Param("groupId") UUID groupId, @Param("category") String category, @Param("periodMonth") String periodMonth);
}
