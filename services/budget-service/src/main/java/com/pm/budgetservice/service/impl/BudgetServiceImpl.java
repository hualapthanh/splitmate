package com.pm.budgetservice.service.impl;

import com.pm.budgetservice.dto.request.CreateBudgetRequest;
import com.pm.budgetservice.dto.request.UpdateBudgetRequest;
import com.pm.budgetservice.dto.response.BudgetResponse;
import com.pm.budgetservice.dto.response.BudgetSummaryResponse;
import com.pm.budgetservice.entity.Budget;
import com.pm.budgetservice.entity.BudgetScope;
import com.pm.budgetservice.event.BudgetAlertEvent;
import com.pm.budgetservice.event.BudgetEventPublisher;
import com.pm.budgetservice.event.ExpenseCreatedEvent;
import com.pm.budgetservice.exception.InvalidBudgetException;
import com.pm.budgetservice.exception.ResourceNotFoundException;
import com.pm.budgetservice.mapper.BudgetMapper;
import com.pm.budgetservice.repository.BudgetRepository;
import com.pm.budgetservice.service.BudgetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final BudgetMapper budgetMapper;
    private final BudgetEventPublisher budgetEventPublisher;

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Override
    @Transactional
    public BudgetResponse createBudget(UUID userId, CreateBudgetRequest request) {
        if (request.getScope() == BudgetScope.GROUP && request.getGroupId() == null) {
            throw new InvalidBudgetException("GroupId is required for GROUP budget scope");
        }

        String category = request.getCategory() != null ? request.getCategory().toUpperCase() : "ALL";

        Optional<Budget> existing = budgetRepository.findByUserIdAndScopeAndGroupIdAndCategoryAndPeriodMonth(
                userId, request.getScope(), request.getGroupId(), category, request.getPeriodMonth()
        );
        if (existing.isPresent()) {
            throw new InvalidBudgetException("A budget for this scope, category, and month already exists");
        }

        Budget budget = budgetMapper.toEntity(request);
        budget.setUserId(userId);
        budget.setCategory(category);
        budget.setCurrentSpent(BigDecimal.ZERO);

        Budget saved = budgetRepository.save(budget);
        log.info("Created budget ID {} for user {} with limit {}", saved.getId(), userId, saved.getAmountLimit());
        return budgetMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public BudgetResponse updateBudget(UUID userId, UUID budgetId, UpdateBudgetRequest request) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with ID: " + budgetId));

        if (!budget.getUserId().equals(userId)) {
            throw new InvalidBudgetException("You are not authorized to update this budget");
        }

        if (request.getAmountLimit() != null) {
            budget.setAmountLimit(request.getAmountLimit());
        }
        if (request.getCategory() != null) {
            budget.setCategory(request.getCategory().toUpperCase());
        }

        Budget updated = budgetRepository.save(budget);
        checkAndSendAlerts(updated);
        return budgetMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteBudget(UUID userId, UUID budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with ID: " + budgetId));

        if (!budget.getUserId().equals(userId)) {
            throw new InvalidBudgetException("You are not authorized to delete this budget");
        }

        budgetRepository.delete(budget);
        log.info("Deleted budget ID {} for user {}", budgetId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public BudgetResponse getBudgetById(UUID userId, UUID budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with ID: " + budgetId));

        if (!budget.getUserId().equals(userId)) {
            throw new InvalidBudgetException("You are not authorized to view this budget");
        }

        return budgetMapper.toResponse(budget);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetResponse> getUserBudgets(UUID userId, String periodMonth) {
        String targetMonth = (periodMonth != null && !periodMonth.isBlank())
                ? periodMonth
                : LocalDate.now().format(MONTH_FORMATTER);

        List<Budget> budgets = budgetRepository.findByUserIdAndPeriodMonth(userId, targetMonth);
        return budgets.stream()
                .map(budgetMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BudgetSummaryResponse getBudgetSummary(UUID userId, String periodMonth) {
        String targetMonth = (periodMonth != null && !periodMonth.isBlank())
                ? periodMonth
                : LocalDate.now().format(MONTH_FORMATTER);

        List<BudgetResponse> budgets = getUserBudgets(userId, targetMonth);

        BigDecimal totalLimit = budgets.stream()
                .map(BudgetResponse::getAmountLimit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSpent = budgets.stream()
                .map(BudgetResponse::getCurrentSpent)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double overallPct = 0.0;
        if (totalLimit.compareTo(BigDecimal.ZERO) > 0) {
            overallPct = totalSpent.multiply(BigDecimal.valueOf(100))
                    .divide(totalLimit, 2, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        return BudgetSummaryResponse.builder()
                .periodMonth(targetMonth)
                .totalBudgets(budgets.size())
                .totalLimitAmount(totalLimit)
                .totalSpentAmount(totalSpent)
                .overallPercentageUsed(overallPct)
                .budgets(budgets)
                .build();
    }

    @Override
    @Transactional
    public void processExpenseCreatedEvent(ExpenseCreatedEvent event) {
        if (event == null) return;

        LocalDate date = event.getDate() != null ? event.getDate() : LocalDate.now();
        String periodMonth = date.format(MONTH_FORMATTER);
        String category = event.getExpenseType() != null ? event.getExpenseType().toUpperCase() : "OTHER";

        // 1. Update Group budgets if groupId present
        if (event.getGroupId() != null && event.getAmount() != null) {
            List<Budget> groupBudgets = budgetRepository.findMatchingGroupBudgets(event.getGroupId(), category, periodMonth);
            for (Budget budget : groupBudgets) {
                budget.setCurrentSpent(budget.getCurrentSpent().add(event.getAmount()));
                budgetRepository.save(budget);
                checkAndSendAlerts(budget);
            }
        }

        // 2. Update Personal budgets for splits
        if (event.getSplits() != null) {
            for (ExpenseCreatedEvent.SplitItem split : event.getSplits()) {
                if (split.getUserId() != null && split.getAmount() != null) {
                    List<Budget> personalBudgets = budgetRepository.findMatchingPersonalBudgets(split.getUserId(), category, periodMonth);
                    for (Budget budget : personalBudgets) {
                        budget.setCurrentSpent(budget.getCurrentSpent().add(split.getAmount()));
                        budgetRepository.save(budget);
                        checkAndSendAlerts(budget);
                    }
                }
            }
        }
    }

    private void checkAndSendAlerts(Budget budget) {
        if (budget.getAmountLimit() == null || budget.getAmountLimit().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        double pct = budget.getCurrentSpent().multiply(BigDecimal.valueOf(100))
                .divide(budget.getAmountLimit(), 2, RoundingMode.HALF_UP)
                .doubleValue();

        if (pct >= 100.0 && !budget.isAlert100Sent()) {
            budget.setAlert100Sent(true);
            budgetRepository.save(budget);
            sendAlertEvent(budget, 100, String.format("Cảnh báo: Ngân sách %s (%s) đã VƯỢT HẠN MỨC (Chi tiêu: %s / %s VND)!",
                    budget.getCategory(), budget.getPeriodMonth(), budget.getCurrentSpent(), budget.getAmountLimit()));
        } else if (pct >= 90.0 && !budget.isAlert90Sent()) {
            budget.setAlert90Sent(true);
            budgetRepository.save(budget);
            sendAlertEvent(budget, 90, String.format("Cảnh báo: Ngân sách %s (%s) đã đạt %.1f%% (Chi tiêu: %s / %s VND)!",
                    budget.getCategory(), budget.getPeriodMonth(), pct, budget.getCurrentSpent(), budget.getAmountLimit()));
        } else if (pct >= 80.0 && !budget.isAlert80Sent()) {
            budget.setAlert80Sent(true);
            budgetRepository.save(budget);
            sendAlertEvent(budget, 80, String.format("Cảnh báo: Ngân sách %s (%s) đã đạt %.1f%% (Chi tiêu: %s / %s VND)!",
                    budget.getCategory(), budget.getPeriodMonth(), pct, budget.getCurrentSpent(), budget.getAmountLimit()));
        }
    }

    private void sendAlertEvent(Budget budget, int thresholdPercent, String message) {
        BudgetAlertEvent alertEvent = BudgetAlertEvent.builder()
                .budgetId(budget.getId())
                .userId(budget.getUserId())
                .groupId(budget.getGroupId())
                .scope(budget.getScope())
                .category(budget.getCategory())
                .thresholdPercent(thresholdPercent)
                .amountLimit(budget.getAmountLimit())
                .currentSpent(budget.getCurrentSpent())
                .periodMonth(budget.getPeriodMonth())
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();

        budgetEventPublisher.publishBudgetAlertEvent(alertEvent);
    }
}
