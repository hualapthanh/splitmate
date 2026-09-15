package com.pm.analyticsservice.service.impl;

import com.pm.analyticsservice.dto.response.*;
import com.pm.analyticsservice.entity.AnalyticsExpenseRecord;
import com.pm.analyticsservice.entity.AnalyticsMonthlySummary;
import com.pm.analyticsservice.event.ExpenseCreatedEvent;
import com.pm.analyticsservice.event.SettlementCreatedEvent;
import com.pm.analyticsservice.repository.AnalyticsExpenseRecordRepository;
import com.pm.analyticsservice.repository.AnalyticsMonthlySummaryRepository;
import com.pm.analyticsservice.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final AnalyticsExpenseRecordRepository recordRepository;
    private final AnalyticsMonthlySummaryRepository summaryRepository;

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Override
    @Transactional(readOnly = true)
    public AnalyticsOverviewResponse getOverview(UUID userId, String periodMonth) {
        String currentMonth = (periodMonth != null && !periodMonth.isBlank())
                ? periodMonth
                : LocalDate.now().format(MONTH_FORMATTER);

        LocalDate currentLocalDate = LocalDate.parse(currentMonth + "-01");
        String prevMonth = currentLocalDate.minusMonths(1).format(MONTH_FORMATTER);

        Optional<AnalyticsMonthlySummary> currentSummary = summaryRepository.findByUserIdAndPeriodMonth(userId, currentMonth);
        Optional<AnalyticsMonthlySummary> prevSummary = summaryRepository.findByUserIdAndPeriodMonth(userId, prevMonth);

        BigDecimal currentSpent = currentSummary.map(AnalyticsMonthlySummary::getTotalSpent).orElse(BigDecimal.ZERO);
        int currentCount = currentSummary.map(AnalyticsMonthlySummary::getTotalTransactions).orElse(0);

        BigDecimal prevSpent = prevSummary.map(AnalyticsMonthlySummary::getTotalSpent).orElse(BigDecimal.ZERO);

        double percentageChange = 0.0;
        if (prevSpent.compareTo(BigDecimal.ZERO) > 0) {
            percentageChange = currentSpent.subtract(prevSpent)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(prevSpent, 2, RoundingMode.HALF_UP)
                    .doubleValue();
        } else if (currentSpent.compareTo(BigDecimal.ZERO) > 0) {
            percentageChange = 100.0;
        }

        List<Object[]> categoryStats = recordRepository.findCategoryStatsByUserAndPeriod(userId, currentMonth);
        String topCategory = "N/A";
        BigDecimal topAmount = BigDecimal.ZERO;

        for (Object[] row : categoryStats) {
            String cat = (String) row[0];
            BigDecimal amt = (BigDecimal) row[1];
            if (amt.compareTo(topAmount) > 0) {
                topAmount = amt;
                topCategory = cat;
            }
        }

        return AnalyticsOverviewResponse.builder()
                .currentPeriodMonth(currentMonth)
                .currentMonthTotalSpent(currentSpent)
                .currentMonthTransactions(currentCount)
                .previousPeriodMonth(prevMonth)
                .previousMonthTotalSpent(prevSpent)
                .percentageChange(percentageChange)
                .topSpendingCategory(topCategory)
                .topSpendingCategoryAmount(topAmount)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryBreakdownResponse getCategoryBreakdown(UUID userId, String periodMonth) {
        String targetMonth = (periodMonth != null && !periodMonth.isBlank())
                ? periodMonth
                : LocalDate.now().format(MONTH_FORMATTER);

        List<Object[]> rows = recordRepository.findCategoryStatsByUserAndPeriod(userId, targetMonth);

        BigDecimal totalSpent = BigDecimal.ZERO;
        List<CategoryBreakdownResponse.CategoryItem> items = new ArrayList<>();

        for (Object[] row : rows) {
            BigDecimal amt = (BigDecimal) row[1];
            totalSpent = totalSpent.add(amt);
        }

        for (Object[] row : rows) {
            String cat = (String) row[0];
            BigDecimal amt = (BigDecimal) row[1];
            long count = (long) row[2];

            double pct = 0.0;
            if (totalSpent.compareTo(BigDecimal.ZERO) > 0) {
                pct = amt.multiply(BigDecimal.valueOf(100))
                        .divide(totalSpent, 2, RoundingMode.HALF_UP)
                        .doubleValue();
            }

            items.add(CategoryBreakdownResponse.CategoryItem.builder()
                    .category(cat)
                    .amount(amt)
                    .percentage(pct)
                    .transactionCount(count)
                    .build());
        }

        return CategoryBreakdownResponse.builder()
                .periodMonth(targetMonth)
                .totalSpent(totalSpent)
                .categories(items)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public SpendingTrendResponse getSpendingTrends(UUID userId) {
        List<Object[]> rows = recordRepository.findMonthlyTrendsByUser(userId);
        List<SpendingTrendResponse.TrendPoint> points = new ArrayList<>();

        for (Object[] row : rows) {
            String month = (String) row[0];
            BigDecimal amt = (BigDecimal) row[1];
            long count = (long) row[2];

            points.add(SpendingTrendResponse.TrendPoint.builder()
                    .periodMonth(month)
                    .totalAmount(amt)
                    .transactionCount(count)
                    .build());
        }

        return SpendingTrendResponse.builder()
                .trends(points)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public GroupAnalyticsResponse getGroupAnalytics(UUID groupId, String periodMonth) {
        String targetMonth = (periodMonth != null && !periodMonth.isBlank())
                ? periodMonth
                : LocalDate.now().format(MONTH_FORMATTER);

        List<Object[]> memberRows = recordRepository.findGroupMemberStats(groupId, targetMonth);

        BigDecimal groupTotal = BigDecimal.ZERO;
        List<GroupAnalyticsResponse.MemberContribution> members = new ArrayList<>();

        for (Object[] row : memberRows) {
            BigDecimal amt = (BigDecimal) row[1];
            groupTotal = groupTotal.add(amt);
        }

        for (Object[] row : memberRows) {
            UUID memberUserId = (UUID) row[0];
            BigDecimal amt = (BigDecimal) row[1];
            long count = (long) row[2];

            double pct = 0.0;
            if (groupTotal.compareTo(BigDecimal.ZERO) > 0) {
                pct = amt.multiply(BigDecimal.valueOf(100))
                        .divide(groupTotal, 2, RoundingMode.HALF_UP)
                        .doubleValue();
            }

            members.add(GroupAnalyticsResponse.MemberContribution.builder()
                    .userId(memberUserId)
                    .amountSpent(amt)
                    .percentageShare(pct)
                    .transactionCount(count)
                    .build());
        }

        List<AnalyticsExpenseRecord> records = recordRepository.findByGroupIdAndPeriodMonth(groupId, targetMonth);
        Map<String, BigDecimal> catTotals = new HashMap<>();
        Map<String, Long> catCounts = new HashMap<>();

        for (AnalyticsExpenseRecord rec : records) {
            catTotals.merge(rec.getCategory(), rec.getAmount(), BigDecimal::add);
            catCounts.merge(rec.getCategory(), 1L, Long::sum);
        }

        List<CategoryBreakdownResponse.CategoryItem> categoryItems = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : catTotals.entrySet()) {
            String cat = entry.getKey();
            BigDecimal amt = entry.getValue();
            long count = catCounts.get(cat);
            double pct = 0.0;
            if (groupTotal.compareTo(BigDecimal.ZERO) > 0) {
                pct = amt.multiply(BigDecimal.valueOf(100))
                        .divide(groupTotal, 2, RoundingMode.HALF_UP)
                        .doubleValue();
            }
            categoryItems.add(CategoryBreakdownResponse.CategoryItem.builder()
                    .category(cat)
                    .amount(amt)
                    .percentage(pct)
                    .transactionCount(count)
                    .build());
        }

        return GroupAnalyticsResponse.builder()
                .groupId(groupId)
                .periodMonth(targetMonth)
                .groupTotalSpent(groupTotal)
                .memberContributions(members)
                .categoryBreakdown(categoryItems)
                .build();
    }

    @Override
    @Transactional
    public void processExpenseCreatedEvent(ExpenseCreatedEvent event) {
        if (event == null) return;

        LocalDate date = event.getDate() != null ? event.getDate() : LocalDate.now();
        String periodMonth = date.format(MONTH_FORMATTER);
        String category = event.getExpenseType() != null ? event.getExpenseType().toUpperCase() : "OTHER";

        if (event.getSplits() != null && !event.getSplits().isEmpty()) {
            for (ExpenseCreatedEvent.SplitItem split : event.getSplits()) {
                if (split.getUserId() != null && split.getAmount() != null) {
                    recordExpense(event.getExpenseId(), split.getUserId(), event.getGroupId(), category, split.getAmount(), date, periodMonth);
                }
            }
        } else if (event.getUserId() != null && event.getAmount() != null) {
            recordExpense(event.getExpenseId(), event.getUserId(), event.getGroupId(), category, event.getAmount(), date, periodMonth);
        }
    }

    @Override
    @Transactional
    public void processSettlementCreatedEvent(SettlementCreatedEvent event) {
        log.info("Settlement event received in AnalyticsService for settlementId: {}", event.getSettlementId());
        // Settlement events can be logged for debt settlement analytics
    }

    private void recordExpense(UUID expenseId, UUID userId, UUID groupId, String category, BigDecimal amount, LocalDate date, String periodMonth) {
        AnalyticsExpenseRecord record = AnalyticsExpenseRecord.builder()
                .expenseId(expenseId)
                .userId(userId)
                .groupId(groupId)
                .category(category)
                .amount(amount)
                .expenseDate(date)
                .periodMonth(periodMonth)
                .build();

        recordRepository.save(record);

        AnalyticsMonthlySummary summary = summaryRepository.findByUserIdAndPeriodMonth(userId, periodMonth)
                .orElseGet(() -> AnalyticsMonthlySummary.builder()
                        .userId(userId)
                        .periodMonth(periodMonth)
                        .totalSpent(BigDecimal.ZERO)
                        .totalTransactions(0)
                        .build());

        summary.setTotalSpent(summary.getTotalSpent().add(amount));
        summary.setTotalTransactions(summary.getTotalTransactions() + 1);

        summaryRepository.save(summary);
    }
}
