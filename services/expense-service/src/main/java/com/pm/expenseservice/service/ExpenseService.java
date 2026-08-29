package com.pm.expenseservice.service;

import com.pm.expenseservice.dto.request.CreateExpenseRequest;
import com.pm.expenseservice.dto.request.UpdateExpenseRequest;
import com.pm.expenseservice.dto.response.ExpenseDetailResponse;
import com.pm.expenseservice.dto.response.ExpenseResponse;

import java.util.List;
import java.util.UUID;

public interface ExpenseService {
    ExpenseDetailResponse createExpense(UUID userId, CreateExpenseRequest request);
    List<ExpenseResponse> getUserExpenses(UUID userId);
    List<ExpenseResponse> getGroupExpenses(UUID userId, UUID groupId);
    ExpenseDetailResponse getExpenseDetails(UUID userId, UUID expenseId);
    ExpenseDetailResponse updateExpense(UUID userId, UUID expenseId, UpdateExpenseRequest request);
    void deleteExpense(UUID userId, UUID expenseId);
}
