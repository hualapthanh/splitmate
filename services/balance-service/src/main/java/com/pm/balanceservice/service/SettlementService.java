package com.pm.balanceservice.service;

import com.pm.balanceservice.dto.request.CreateSettlementRequest;
import com.pm.balanceservice.dto.response.SettlementResponse;

import java.util.List;
import java.util.UUID;

public interface SettlementService {
    SettlementResponse createSettlement(UUID payerId, CreateSettlementRequest request);
    List<SettlementResponse> getGroupSettlements(UUID groupId);
    List<SettlementResponse> getUserSettlements(UUID userId);
}
