package com.pm.budgetservice.service;

import com.pm.budgetservice.dto.request.CreateBudgetRequest;
import com.pm.budgetservice.dto.request.UpdateBudgetRequest;
import com.pm.budgetservice.dto.response.BudgetResponse;
import com.pm.budgetservice.dto.response.BudgetSummaryResponse;
import com.pm.budgetservice.event.ExpenseCreatedEvent;

import java.util.List;
import java.util.UUID;

public interface BudgetService {

    BudgetResponse createBudget(UUID userId, CreateBudgetRequest request);

    BudgetResponse updateBudget(UUID userId, UUID budgetId, UpdateBudgetRequest request);

    void deleteBudget(UUID userId, UUID budgetId);

    BudgetResponse getBudgetById(UUID userId, UUID budgetId);

    List<BudgetResponse> getUserBudgets(UUID userId, String periodMonth);

    BudgetSummaryResponse getBudgetSummary(UUID userId, String periodMonth);

    void processExpenseCreatedEvent(ExpenseCreatedEvent event);
}
