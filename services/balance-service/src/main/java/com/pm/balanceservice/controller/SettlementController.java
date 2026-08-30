package com.pm.balanceservice.controller;

import com.pm.balanceservice.dto.request.CreateSettlementRequest;
import com.pm.balanceservice.dto.response.SettlementResponse;
import com.pm.balanceservice.security.UserPrincipal;
import com.pm.balanceservice.security.annotation.CurrentUser;
import com.pm.balanceservice.service.SettlementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/settlements")
@RequiredArgsConstructor
@Tag(name = "Settlement Management", description = "Endpoints for recording debt settlements and retrieving payment histories")
public class SettlementController {

    private final SettlementService settlementService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Record debt settlement payment", description = "Records a settlement payment to settle debts between users. Updates net balances and publishes Kafka event.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Settlement recorded successfully"),
        @ApiResponse(responseCode = "400", description = "Validation error or cannot settle with self")
    })
    public SettlementResponse createSettlement(
            @CurrentUser UserPrincipal principal,
            @Valid @RequestBody CreateSettlementRequest request
    ) {
        return settlementService.createSettlement(principal.getUserId(), request);
    }

    @GetMapping("/group/{groupId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get group settlements history", description = "Returns history of all settlements recorded in a group.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Group settlements retrieved successfully")
    })
    public List<SettlementResponse> getGroupSettlements(
            @CurrentUser UserPrincipal principal,
            @PathVariable("groupId") UUID groupId
    ) {
        return settlementService.getGroupSettlements(groupId);
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get user settlements history", description = "Returns settlements involving the authenticated user as payer or payee.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User settlements retrieved successfully")
    })
    public List<SettlementResponse> getUserSettlements(@CurrentUser UserPrincipal principal) {
        return settlementService.getUserSettlements(principal.getUserId());
    }
}
