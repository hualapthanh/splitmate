package com.pm.balanceservice.algorithm;

import com.pm.balanceservice.dto.response.SimplifiedDebtResponse;
import com.pm.balanceservice.entity.GroupBalance;
import com.pm.balanceservice.repository.GroupBalanceRepository;
import com.pm.balanceservice.service.impl.BalanceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MinCashFlowAlgorithmTest {

    @Mock
    private GroupBalanceRepository groupBalanceRepository;

    @InjectMocks
    private BalanceServiceImpl balanceService;

    private UUID groupId;
    private UUID userA;
    private UUID userB;
    private UUID userC;

    @BeforeEach
    void setUp() {
        groupId = UUID.randomUUID();
        userA = UUID.randomUUID();
        userB = UUID.randomUUID();
        userC = UUID.randomUUID();
    }

    @Test
    @DisplayName("getSimplifiedDebts() should simplify A owed 100k, B balanced, C owes 100k into 1 transaction C->A")
    void minCashFlow_simplifiesTransactions_success() {
        GroupBalance gbA = GroupBalance.builder().groupId(groupId).userId(userA).netBalance(new BigDecimal("100000.00")).build();
        GroupBalance gbB = GroupBalance.builder().groupId(groupId).userId(userB).netBalance(BigDecimal.ZERO).build();
        GroupBalance gbC = GroupBalance.builder().groupId(groupId).userId(userC).netBalance(new BigDecimal("-100000.00")).build();

        when(groupBalanceRepository.findByGroupId(groupId)).thenReturn(Arrays.asList(gbA, gbB, gbC));

        List<SimplifiedDebtResponse> result = balanceService.getSimplifiedDebts(groupId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userC, result.get(0).getDebtorId());
        assertEquals(userA, result.get(0).getCreditorId());
        assertEquals(new BigDecimal("100000.00"), result.get(0).getAmount());
    }
}
