package com.pm.balanceservice.service;

import com.pm.balanceservice.dto.response.GroupBalanceResponse;
import com.pm.balanceservice.dto.response.SimplifiedDebtResponse;
import com.pm.balanceservice.dto.response.UserDebtResponse;
import com.pm.balanceservice.event.ExpenseCreatedEvent;

import java.util.List;
import java.util.UUID;

public interface BalanceService {
    void processExpenseCreatedEvent(ExpenseCreatedEvent event);
    List<GroupBalanceResponse> getGroupBalances(UUID groupId);
    List<GroupBalanceResponse> getUserBalances(UUID userId);
    List<UserDebtResponse> getActiveDebts(UUID groupId);
    List<SimplifiedDebtResponse> getSimplifiedDebts(UUID groupId);
}
