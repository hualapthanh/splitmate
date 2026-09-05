package com.pm.budgetservice.service;

import com.pm.budgetservice.dto.request.CreateBudgetRequest;
import com.pm.budgetservice.dto.request.UpdateBudgetRequest;
import com.pm.budgetservice.dto.response.BudgetResponse;
import com.pm.budgetservice.dto.response.BudgetSummaryResponse;
import com.pm.budgetservice.entity.Budget;
import com.pm.budgetservice.entity.BudgetScope;
import com.pm.budgetservice.event.BudgetEventPublisher;
import com.pm.budgetservice.event.ExpenseCreatedEvent;
import com.pm.budgetservice.exception.InvalidBudgetException;
import com.pm.budgetservice.exception.ResourceNotFoundException;
import com.pm.budgetservice.mapper.BudgetMapperImpl;
import com.pm.budgetservice.repository.BudgetRepository;
import com.pm.budgetservice.service.impl.BudgetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private BudgetEventPublisher budgetEventPublisher;

    private BudgetService budgetService;

    private UUID userId;
    private UUID groupId;

    @BeforeEach
    void setUp() {
        budgetService = new BudgetServiceImpl(budgetRepository, new BudgetMapperImpl(), budgetEventPublisher);
        userId = UUID.randomUUID();
        groupId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should create personal budget successfully")
    void createBudget_Success() {
        CreateBudgetRequest request = CreateBudgetRequest.builder()
                .scope(BudgetScope.PERSONAL)
                .category("FOOD")
                .amountLimit(new BigDecimal("5000000.00"))
                .periodMonth("2026-09")
                .build();

        when(budgetRepository.findByUserIdAndScopeAndGroupIdAndCategoryAndPeriodMonth(any(), any(), any(), any(), any()))
                .thenReturn(Optional.empty());

        when(budgetRepository.save(any(Budget.class))).thenAnswer(invocation -> {
            Budget b = invocation.getArgument(0);
            b.setId(UUID.randomUUID());
            return b;
        });

        BudgetResponse response = budgetService.createBudget(userId, request);

        assertThat(response).isNotNull();
        assertThat(response.getScope()).isEqualTo(BudgetScope.PERSONAL);
        assertThat(response.getCategory()).isEqualTo("FOOD");
        assertThat(response.getAmountLimit()).isEqualByComparingTo("5000000.00");
        assertThat(response.getCurrentSpent()).isEqualByComparingTo("0.00");
        assertThat(response.getStatus()).isEqualTo("NORMAL");
    }

    @Test
    @DisplayName("Should throw exception when creating group budget without groupId")
    void createBudget_GroupWithoutGroupId_ThrowsException() {
        CreateBudgetRequest request = CreateBudgetRequest.builder()
                .scope(BudgetScope.GROUP)
                .groupId(null)
                .category("ALL")
                .amountLimit(new BigDecimal("10000000.00"))
                .periodMonth("2026-09")
                .build();

        assertThatThrownBy(() -> budgetService.createBudget(userId, request))
                .isInstanceOf(InvalidBudgetException.class)
                .hasMessageContaining("GroupId is required");
    }

    @Test
    @DisplayName("Should update budget and trigger alert when threshold 80% is crossed")
    void processExpenseCreatedEvent_TriggersThresholdAlert() {
        Budget budget = Budget.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .scope(BudgetScope.PERSONAL)
                .category("ALL")
                .amountLimit(new BigDecimal("1000000.00"))
                .currentSpent(new BigDecimal("700000.00")) // 70% currently
                .periodMonth("2026-09")
                .alert80Sent(false)
                .alert90Sent(false)
                .alert100Sent(false)
                .build();

        when(budgetRepository.findMatchingPersonalBudgets(eq(userId), anyString(), eq("2026-09")))
                .thenReturn(List.of(budget));

        ExpenseCreatedEvent event = ExpenseCreatedEvent.builder()
                .expenseId(UUID.randomUUID())
                .userId(userId)
                .expenseType("FOOD")
                .amount(new BigDecimal("150000.00"))
                .date(LocalDate.of(2026, 9, 4))
                .splits(List.of(
                        ExpenseCreatedEvent.SplitItem.builder()
                                .userId(userId)
                                .amount(new BigDecimal("150000.00")) // Total spent now = 850,000 (85%)
                                .build()
                ))
                .build();

        budgetService.processExpenseCreatedEvent(event);

        assertThat(budget.getCurrentSpent()).isEqualByComparingTo("850000.00");
        assertThat(budget.isAlert80Sent()).isTrue();
        assertThat(budget.isAlert90Sent()).isFalse();

        verify(budgetEventPublisher, times(1)).publishBudgetAlertEvent(any());
    }

    @Test
    @DisplayName("Should return summary of user budgets")
    void getBudgetSummary_Success() {
        Budget b1 = Budget.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .scope(BudgetScope.PERSONAL)
                .category("FOOD")
                .amountLimit(new BigDecimal("3000000.00"))
                .currentSpent(new BigDecimal("1500000.00"))
                .periodMonth("2026-09")
                .build();

        Budget b2 = Budget.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .scope(BudgetScope.PERSONAL)
                .category("ENTERTAINMENT")
                .amountLimit(new BigDecimal("2000000.00"))
                .currentSpent(new BigDecimal("500000.00"))
                .periodMonth("2026-09")
                .build();

        when(budgetRepository.findByUserIdAndPeriodMonth(userId, "2026-09"))
                .thenReturn(List.of(b1, b2));

        BudgetSummaryResponse summary = budgetService.getBudgetSummary(userId, "2026-09");

        assertThat(summary).isNotNull();
        assertThat(summary.getTotalBudgets()).isEqualTo(2);
        assertThat(summary.getTotalLimitAmount()).isEqualByComparingTo("5000000.00");
        assertThat(summary.getTotalSpentAmount()).isEqualByComparingTo("2000000.00");
        assertThat(summary.getOverallPercentageUsed()).isEqualTo(40.0);
    }
}
