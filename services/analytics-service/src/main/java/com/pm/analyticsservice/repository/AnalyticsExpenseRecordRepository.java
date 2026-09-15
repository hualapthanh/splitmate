package com.pm.analyticsservice.repository;

import com.pm.analyticsservice.entity.AnalyticsExpenseRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnalyticsExpenseRecordRepository extends JpaRepository<AnalyticsExpenseRecord, UUID> {

    List<AnalyticsExpenseRecord> findByUserIdAndPeriodMonth(UUID userId, String periodMonth);

    List<AnalyticsExpenseRecord> findByGroupIdAndPeriodMonth(UUID groupId, String periodMonth);

    @Query("SELECT r.category, SUM(r.amount), COUNT(r) FROM AnalyticsExpenseRecord r WHERE r.userId = :userId AND r.periodMonth = :periodMonth GROUP BY r.category")
    List<Object[]> findCategoryStatsByUserAndPeriod(@Param("userId") UUID userId, @Param("periodMonth") String periodMonth);

    @Query("SELECT r.periodMonth, SUM(r.amount), COUNT(r) FROM AnalyticsExpenseRecord r WHERE r.userId = :userId GROUP BY r.periodMonth ORDER BY r.periodMonth ASC")
    List<Object[]> findMonthlyTrendsByUser(@Param("userId") UUID userId);

    @Query("SELECT r.userId, SUM(r.amount), COUNT(r) FROM AnalyticsExpenseRecord r WHERE r.groupId = :groupId AND r.periodMonth = :periodMonth GROUP BY r.userId")
    List<Object[]> findGroupMemberStats(@Param("groupId") UUID groupId, @Param("periodMonth") String periodMonth);

    Optional<AnalyticsExpenseRecord> findByExpenseIdAndUserId(UUID expenseId, UUID userId);
}
