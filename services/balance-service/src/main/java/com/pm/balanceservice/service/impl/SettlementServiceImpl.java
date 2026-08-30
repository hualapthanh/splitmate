package com.pm.balanceservice.service.impl;

import com.pm.balanceservice.dto.request.CreateSettlementRequest;
import com.pm.balanceservice.dto.response.SettlementResponse;
import com.pm.balanceservice.entity.GroupBalance;
import com.pm.balanceservice.entity.Settlement;
import com.pm.balanceservice.event.SettlementCreatedEvent;
import com.pm.balanceservice.event.SettlementEventPublisher;
import com.pm.balanceservice.exception.BusinessException;
import com.pm.balanceservice.exception.ErrorCode;
import com.pm.balanceservice.mapper.BalanceMapper;
import com.pm.balanceservice.repository.GroupBalanceRepository;
import com.pm.balanceservice.repository.SettlementRepository;
import com.pm.balanceservice.service.SettlementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementServiceImpl implements SettlementService {

    private final SettlementRepository settlementRepository;
    private final GroupBalanceRepository groupBalanceRepository;
    private final BalanceMapper balanceMapper;
    private final SettlementEventPublisher settlementEventPublisher;

    @Override
    @Transactional
    public SettlementResponse createSettlement(UUID payerId, CreateSettlementRequest request) {
        if (payerId.equals(request.getPayeeId())) {
            throw new BusinessException(ErrorCode.BAL_004, "Payer and payee cannot be the same user");
        }

        log.info("Creating settlement of {} from payerId: {} to payeeId: {}", request.getAmount(), payerId, request.getPayeeId());

        Settlement settlement = Settlement.builder()
                .groupId(request.getGroupId())
                .payerId(payerId)
                .payeeId(request.getPayeeId())
                .amount(request.getAmount())
                .currency(request.getCurrency() != null ? request.getCurrency() : "VND")
                .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "BANK_TRANSFER")
                .status("COMPLETED")
                .build();

        Settlement savedSettlement = settlementRepository.save(settlement);

        // Update group net balances if groupId is present
        if (request.getGroupId() != null) {
            updateUserNetBalance(request.getGroupId(), payerId, request.getAmount());
            updateUserNetBalance(request.getGroupId(), request.getPayeeId(), request.getAmount().negate());
        }

        // Publish SettlementCreatedEvent to Kafka
        SettlementCreatedEvent event = SettlementCreatedEvent.builder()
                .settlementId(savedSettlement.getId())
                .groupId(savedSettlement.getGroupId())
                .payerId(savedSettlement.getPayerId())
                .payeeId(savedSettlement.getPayeeId())
                .amount(savedSettlement.getAmount())
                .currency(savedSettlement.getCurrency())
                .paymentMethod(savedSettlement.getPaymentMethod())
                .settledAt(savedSettlement.getSettledAt())
                .build();

        settlementEventPublisher.publishSettlementCreatedEvent(event);

        log.info("Settlement created successfully with id: {}", savedSettlement.getId());
        return balanceMapper.toSettlementResponse(savedSettlement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SettlementResponse> getGroupSettlements(UUID groupId) {
        List<Settlement> settlements = settlementRepository.findByGroupIdOrderBySettledAtDesc(groupId);
        return balanceMapper.toSettlementResponseList(settlements);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SettlementResponse> getUserSettlements(UUID userId) {
        List<Settlement> settlements = settlementRepository.findByPayerIdOrPayeeIdOrderBySettledAtDesc(userId, userId);
        return balanceMapper.toSettlementResponseList(settlements);
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
}
