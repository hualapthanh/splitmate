package com.pm.balanceservice.service;

import com.pm.balanceservice.dto.request.CreateSettlementRequest;
import com.pm.balanceservice.dto.response.SettlementResponse;
import com.pm.balanceservice.entity.Settlement;
import com.pm.balanceservice.event.SettlementEventPublisher;
import com.pm.balanceservice.exception.BusinessException;
import com.pm.balanceservice.mapper.BalanceMapper;
import com.pm.balanceservice.repository.GroupBalanceRepository;
import com.pm.balanceservice.repository.SettlementRepository;
import com.pm.balanceservice.service.impl.SettlementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

    @Mock
    private SettlementRepository settlementRepository;

    @Mock
    private GroupBalanceRepository groupBalanceRepository;

    @Mock
    private BalanceMapper balanceMapper;

    @Mock
    private SettlementEventPublisher settlementEventPublisher;

    @InjectMocks
    private SettlementServiceImpl settlementService;

    private UUID samplePayerId;
    private UUID samplePayeeId;
    private UUID sampleGroupId;

    @BeforeEach
    void setUp() {
        samplePayerId = UUID.randomUUID();
        samplePayeeId = UUID.randomUUID();
        sampleGroupId = UUID.randomUUID();
    }

    @Test
    @DisplayName("createSettlement() should save settlement, update balances, and publish Kafka event")
    void createSettlement_success() {
        CreateSettlementRequest request = CreateSettlementRequest.builder()
                .groupId(sampleGroupId)
                .payeeId(samplePayeeId)
                .amount(new BigDecimal("100000.00"))
                .currency("VND")
                .paymentMethod("BANK_TRANSFER")
                .build();

        Settlement settlement = Settlement.builder()
                .id(UUID.randomUUID())
                .groupId(sampleGroupId)
                .payerId(samplePayerId)
                .payeeId(samplePayeeId)
                .amount(new BigDecimal("100000.00"))
                .build();

        SettlementResponse expectedResponse = SettlementResponse.builder()
                .id(settlement.getId())
                .amount(new BigDecimal("100000.00"))
                .build();

        when(settlementRepository.save(any(Settlement.class))).thenReturn(settlement);
        when(balanceMapper.toSettlementResponse(settlement)).thenReturn(expectedResponse);

        SettlementResponse response = settlementService.createSettlement(samplePayerId, request);

        assertNotNull(response);
        assertEquals(new BigDecimal("100000.00"), response.getAmount());
        verify(settlementRepository).save(any(Settlement.class));
        verify(settlementEventPublisher).publishSettlementCreatedEvent(any());
    }

    @Test
    @DisplayName("createSettlement() should throw BusinessException when payerId equals payeeId")
    void createSettlement_sameUser_throwsException() {
        CreateSettlementRequest request = CreateSettlementRequest.builder()
                .payeeId(samplePayerId)
                .amount(new BigDecimal("100000.00"))
                .build();

        assertThrows(BusinessException.class, () -> settlementService.createSettlement(samplePayerId, request));
    }
}
