package com.pm.analyticsservice.service;

import com.pm.analyticsservice.dto.response.AnalyticsOverviewResponse;
import com.pm.analyticsservice.dto.response.CategoryBreakdownResponse;
import com.pm.analyticsservice.entity.AnalyticsExpenseRecord;
import com.pm.analyticsservice.repository.AnalyticsExpenseRecordRepository;
import com.pm.analyticsservice.service.impl.ReportExportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportExportServiceTest {

    @Mock
    private AnalyticsExpenseRecordRepository recordRepository;

    @Mock
    private AnalyticsService analyticsService;

    private ReportExportService reportExportService;

    private UUID userId;

    @BeforeEach
    void setUp() {
        reportExportService = new ReportExportServiceImpl(recordRepository, analyticsService);
        userId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should generate CSV report successfully")
    void exportCsvReport_Success() {
        AnalyticsExpenseRecord rec1 = AnalyticsExpenseRecord.builder()
                .expenseId(UUID.randomUUID())
                .userId(userId)
                .category("FOOD")
                .amount(new BigDecimal("150000.00"))
                .expenseDate(LocalDate.of(2026, 9, 5))
                .periodMonth("2026-09")
                .build();

        when(recordRepository.findByUserIdAndPeriodMonth(userId, "2026-09")).thenReturn(List.of(rec1));

        byte[] csvBytes = reportExportService.exportCsvReport(userId, "2026-09");

        assertThat(csvBytes).isNotNull().isNotEmpty();
        String csvText = new String(csvBytes, StandardCharsets.UTF_8);
        assertThat(csvText).contains("Date,Category,Amount (VND),Expense ID");
        assertThat(csvText).contains("FOOD");
        assertThat(csvText).contains("150000.00");
    }

    @Test
    @DisplayName("Should generate PDF report successfully")
    void exportPdfReport_Success() {
        AnalyticsOverviewResponse overview = AnalyticsOverviewResponse.builder()
                .currentPeriodMonth("2026-09")
                .currentMonthTotalSpent(new BigDecimal("2000000.00"))
                .currentMonthTransactions(4)
                .topSpendingCategory("FOOD")
                .topSpendingCategoryAmount(new BigDecimal("1200000.00"))
                .percentageChange(10.0)
                .build();

        CategoryBreakdownResponse breakdown = CategoryBreakdownResponse.builder()
                .periodMonth("2026-09")
                .totalSpent(new BigDecimal("2000000.00"))
                .categories(List.of(
                        CategoryBreakdownResponse.CategoryItem.builder()
                                .category("FOOD")
                                .amount(new BigDecimal("1200000.00"))
                                .percentage(60.0)
                                .transactionCount(3)
                                .build()
                ))
                .build();

        when(analyticsService.getOverview(userId, "2026-09")).thenReturn(overview);
        when(analyticsService.getCategoryBreakdown(userId, "2026-09")).thenReturn(breakdown);
        when(recordRepository.findByUserIdAndPeriodMonth(userId, "2026-09")).thenReturn(List.of());

        byte[] pdfBytes = reportExportService.exportPdfReport(userId, "2026-09");

        assertThat(pdfBytes).isNotNull().isNotEmpty();
        // Check PDF header signature magic bytes '%PDF'
        String header = new String(pdfBytes, 0, 4, StandardCharsets.ISO_8859_1);
        assertThat(header).isEqualTo("%PDF");
    }
}
