package com.pm.analyticsservice.service;

import com.pm.analyticsservice.dto.response.AnalyticsOverviewResponse;
import com.pm.analyticsservice.dto.response.CategoryBreakdownResponse;
import com.pm.analyticsservice.dto.response.GroupAnalyticsResponse;
import com.pm.analyticsservice.dto.response.SpendingTrendResponse;
import com.pm.analyticsservice.event.ExpenseCreatedEvent;
import com.pm.analyticsservice.event.SettlementCreatedEvent;

import java.util.UUID;

public interface AnalyticsService {

    AnalyticsOverviewResponse getOverview(UUID userId, String periodMonth);

    CategoryBreakdownResponse getCategoryBreakdown(UUID userId, String periodMonth);

    SpendingTrendResponse getSpendingTrends(UUID userId);

    GroupAnalyticsResponse getGroupAnalytics(UUID groupId, String periodMonth);

    void processExpenseCreatedEvent(ExpenseCreatedEvent event);

    void processSettlementCreatedEvent(SettlementCreatedEvent event);
}
