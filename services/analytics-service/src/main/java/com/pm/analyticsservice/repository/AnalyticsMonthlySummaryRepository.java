package com.pm.analyticsservice.repository;

import com.pm.analyticsservice.entity.AnalyticsMonthlySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnalyticsMonthlySummaryRepository extends JpaRepository<AnalyticsMonthlySummary, UUID> {

    Optional<AnalyticsMonthlySummary> findByUserIdAndPeriodMonth(UUID userId, String periodMonth);
}
