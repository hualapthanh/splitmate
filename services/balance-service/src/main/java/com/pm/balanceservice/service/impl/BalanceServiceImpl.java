package com.pm.balanceservice.service.impl;

import com.pm.balanceservice.dto.response.GroupBalanceResponse;
import com.pm.balanceservice.dto.response.SimplifiedDebtResponse;
import com.pm.balanceservice.dto.response.UserDebtResponse;
import com.pm.balanceservice.entity.GroupBalance;
import com.pm.balanceservice.entity.UserDebt;
import com.pm.balanceservice.event.ExpenseCreatedEvent;
import com.pm.balanceservice.mapper.BalanceMapper;
import com.pm.balanceservice.repository.GroupBalanceRepository;
import com.pm.balanceservice.repository.UserDebtRepository;
import com.pm.balanceservice.service.BalanceService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final GroupBalanceRepository groupBalanceRepository;
    private final UserDebtRepository userDebtRepository;
    private final BalanceMapper balanceMapper;

    @Override
    @Transactional
    public void processExpenseCreatedEvent(ExpenseCreatedEvent event) {
        UUID groupId = event.getGroupId();
        if (groupId == null) {
            log.info("Expense {} is personal, skipping group balance processing", event.getExpenseId());
            return;
        }

        log.info("Processing ExpenseCreatedEvent for expenseId: {} in groupId: {}", event.getExpenseId(), groupId);

        // 1. Process Payers (+net_balance)
        if (event.getPayers() != null) {
            for (ExpenseCreatedEvent.PayerItem payer : event.getPayers()) {
                updateUserNetBalance(groupId, payer.getUserId(), payer.getAmountPaid());
            }
        }

        // 2. Process Splits (-net_balance)
        if (event.getSplits() != null) {
            for (ExpenseCreatedEvent.SplitItem split : event.getSplits()) {
                updateUserNetBalance(groupId, split.getUserId(), split.getAmount().negate());
            }
        }

        log.info("Updated group balances successfully for groupId: {}", groupId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupBalanceResponse> getGroupBalances(UUID groupId) {
        List<GroupBalance> balances = groupBalanceRepository.findByGroupId(groupId);
        return balanceMapper.toGroupBalanceResponseList(balances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupBalanceResponse> getUserBalances(UUID userId) {
        List<GroupBalance> balances = groupBalanceRepository.findByUserId(userId);
        return balanceMapper.toGroupBalanceResponseList(balances);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDebtResponse> getActiveDebts(UUID groupId) {
        List<UserDebt> activeDebts = userDebtRepository.findByGroupIdAndStatus(groupId, "ACTIVE");
        return balanceMapper.toUserDebtResponseList(activeDebts);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SimplifiedDebtResponse> getSimplifiedDebts(UUID groupId) {
        List<GroupBalance> groupBalances = groupBalanceRepository.findByGroupId(groupId);

        // Min-Cash-Flow Greedy Algorithm
        // 1. Separate creditors (net > 0) and debtors (net < 0)
        PriorityQueue<UserBalanceNode> maxCreditors = new PriorityQueue<>(Comparator.comparing(UserBalanceNode::getAmount).reversed());
        PriorityQueue<UserBalanceNode> maxDebtors = new PriorityQueue<>(Comparator.comparing(UserBalanceNode::getAmount)); // absolute negative sorted

        for (GroupBalance gb : groupBalances) {
            BigDecimal net = gb.getNetBalance();
            if (net.compareTo(BigDecimal.ZERO) > 0) {
                maxCreditors.add(new UserBalanceNode(gb.getUserId(), net));
            } else if (net.compareTo(BigDecimal.ZERO) < 0) {
                maxDebtors.add(new UserBalanceNode(gb.getUserId(), net.abs()));
            }
        }

        List<SimplifiedDebtResponse> result = new ArrayList<>();

        while (!maxCreditors.isEmpty() && !maxDebtors.isEmpty()) {
            UserBalanceNode creditor = maxCreditors.poll();
            UserBalanceNode debtor = maxDebtors.poll();

            BigDecimal minAmount = creditor.getAmount().min(debtor.getAmount());

            result.add(SimplifiedDebtResponse.builder()
                    .debtorId(debtor.getUserId())
                    .creditorId(creditor.getUserId())
                    .amount(minAmount)
                    .build());

            creditor.setAmount(creditor.getAmount().subtract(minAmount));
            debtor.setAmount(debtor.getAmount().subtract(minAmount));

            if (creditor.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                maxCreditors.add(creditor);
            }
            if (debtor.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                maxDebtors.add(debtor);
            }
        }

        return result;
    }

    private void updateUserNetBalance(UUID groupId, UUID userId, BigDecimal delta) {
        GroupBalance balance = groupBalanceRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseGet(() -> GroupBalance.builder()
                        .groupId(groupId)
                        .userId(userId)
                        .netBalance(BigDecimal.ZERO)
                        .build());

        balance.setNetBalance(balance.getNetBalance().add(delta));
        groupBalanceRepository.save(balance);
    }

    @Data
    @AllArgsConstructor
    private static class UserBalanceNode {
        private UUID userId;
        private BigDecimal amount;
    }
}
