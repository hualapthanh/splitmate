package com.pm.analyticsservice.service;

import com.pm.analyticsservice.dto.response.AnalyticsOverviewResponse;
import com.pm.analyticsservice.dto.response.CategoryBreakdownResponse;
import com.pm.analyticsservice.dto.response.SpendingTrendResponse;
import com.pm.analyticsservice.entity.AnalyticsExpenseRecord;
import com.pm.analyticsservice.entity.AnalyticsMonthlySummary;
import com.pm.analyticsservice.event.ExpenseCreatedEvent;
import com.pm.analyticsservice.repository.AnalyticsExpenseRecordRepository;
import com.pm.analyticsservice.repository.AnalyticsMonthlySummaryRepository;
import com.pm.analyticsservice.service.impl.AnalyticsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private AnalyticsExpenseRecordRepository recordRepository;

    @Mock
    private AnalyticsMonthlySummaryRepository summaryRepository;

    private AnalyticsService analyticsService;

    private UUID userId;

    @BeforeEach
    void setUp() {
        analyticsService = new AnalyticsServiceImpl(recordRepository, summaryRepository);
        userId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should return overview response with correct percentage change")
    void getOverview_Success() {
        AnalyticsMonthlySummary currentSummary = AnalyticsMonthlySummary.builder()
                .userId(userId)
                .periodMonth("2026-09")
                .totalSpent(new BigDecimal("5000000.00"))
                .totalTransactions(10)
                .build();

        AnalyticsMonthlySummary prevSummary = AnalyticsMonthlySummary.builder()
                .userId(userId)
                .periodMonth("2026-08")
                .totalSpent(new BigDecimal("4000000.00"))
                .totalTransactions(8)
                .build();

        when(summaryRepository.findByUserIdAndPeriodMonth(userId, "2026-09")).thenReturn(Optional.of(currentSummary));
        when(summaryRepository.findByUserIdAndPeriodMonth(userId, "2026-08")).thenReturn(Optional.of(prevSummary));

        Object[] row1 = new Object[]{"FOOD", new BigDecimal("3000000.00"), 6L};
        Object[] row2 = new Object[]{"ENTERTAINMENT", new BigDecimal("2000000.00"), 4L};
        when(recordRepository.findCategoryStatsByUserAndPeriod(userId, "2026-09")).thenReturn(List.of(row1, row2));

        AnalyticsOverviewResponse overview = analyticsService.getOverview(userId, "2026-09");

        assertThat(overview).isNotNull();
        assertThat(overview.getCurrentPeriodMonth()).isEqualTo("2026-09");
        assertThat(overview.getCurrentMonthTotalSpent()).isEqualByComparingTo("5000000.00");
        assertThat(overview.getPreviousMonthTotalSpent()).isEqualByComparingTo("4000000.00");
        assertThat(overview.getPercentageChange()).isEqualTo(25.0); // (5m - 4m) / 4m = +25%
        assertThat(overview.getTopSpendingCategory()).isEqualTo("FOOD");
        assertThat(overview.getTopSpendingCategoryAmount()).isEqualByComparingTo("3000000.00");
    }

    @Test
    @DisplayName("Should process ExpenseCreatedEvent and update analytics records and summary")
    void processExpenseCreatedEvent_Success() {
        ExpenseCreatedEvent event = ExpenseCreatedEvent.builder()
                .expenseId(UUID.randomUUID())
                .userId(userId)
                .expenseType("FOOD")
                .amount(new BigDecimal("500000.00"))
                .date(LocalDate.of(2026, 9, 5))
                .splits(List.of(
                        ExpenseCreatedEvent.SplitItem.builder()
                                .userId(userId)
                                .amount(new BigDecimal("500000.00"))
                                .build()
                ))
                .build();

        when(summaryRepository.findByUserIdAndPeriodMonth(userId, "2026-09")).thenReturn(Optional.empty());

        analyticsService.processExpenseCreatedEvent(event);

        verify(recordRepository, times(1)).save(any(AnalyticsExpenseRecord.class));
        verify(summaryRepository, times(1)).save(any(AnalyticsMonthlySummary.class));
    }

    @Test
    @DisplayName("Should return category breakdown with percentages")
    void getCategoryBreakdown_Success() {
        Object[] row1 = new Object[]{"FOOD", new BigDecimal("600000.00"), 3L};
        Object[] row2 = new Object[]{"TRANSPORT", new BigDecimal("400000.00"), 2L};

        when(recordRepository.findCategoryStatsByUserAndPeriod(userId, "2026-09")).thenReturn(List.of(row1, row2));

        CategoryBreakdownResponse breakdown = analyticsService.getCategoryBreakdown(userId, "2026-09");

        assertThat(breakdown).isNotNull();
        assertThat(breakdown.getTotalSpent()).isEqualByComparingTo("1000000.00");
        assertThat(breakdown.getCategories()).hasSize(2);
        assertThat(breakdown.getCategories().get(0).getCategory()).isEqualTo("FOOD");
        assertThat(breakdown.getCategories().get(0).getPercentage()).isEqualTo(60.0);
        assertThat(breakdown.getCategories().get(1).getCategory()).isEqualTo("TRANSPORT");
        assertThat(breakdown.getCategories().get(1).getPercentage()).isEqualTo(40.0);
    }
}
