package com.pm.balanceservice.controller;

import com.pm.balanceservice.dto.response.GroupBalanceResponse;
import com.pm.balanceservice.dto.response.SimplifiedDebtResponse;
import com.pm.balanceservice.dto.response.UserDebtResponse;
import com.pm.balanceservice.security.UserPrincipal;
import com.pm.balanceservice.security.annotation.CurrentUser;
import com.pm.balanceservice.service.BalanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/balances")
@RequiredArgsConstructor
@Tag(name = "Balance & Debt Management", description = "Endpoints for net balance queries, pair-wise debts, and Min-Cash-Flow debt simplification")
public class BalanceController {

    private final BalanceService balanceService;

    @GetMapping("/group/{groupId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get group net balances", description = "Returns net balance (+ = owed money, - = owes money) for each member of a group.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Group balances retrieved successfully")
    })
    public List<GroupBalanceResponse> getGroupBalances(
            @CurrentUser UserPrincipal principal,
            @PathVariable("groupId") UUID groupId
    ) {
        return balanceService.getGroupBalances(groupId);
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get user's net balances", description = "Returns authenticated user's net balances across all groups.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User balances retrieved successfully")
    })
    public List<GroupBalanceResponse> getUserBalances(@CurrentUser UserPrincipal principal) {
        return balanceService.getUserBalances(principal.getUserId());
    }

    @GetMapping("/group/{groupId}/debts")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get active pair-wise debts", description = "Returns pair-wise debt relationships between members of a group.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Active debts retrieved successfully")
    })
    public List<UserDebtResponse> getActiveDebts(
            @CurrentUser UserPrincipal principal,
            @PathVariable("groupId") UUID groupId
    ) {
        return balanceService.getActiveDebts(groupId);
    }

    @GetMapping("/group/{groupId}/simplified-debts")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get Min-Cash-Flow simplified debts", description = "Applies Min-Cash-Flow greedy algorithm to minimize transactions required to settle all debts in a group.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Simplified debts calculated successfully")
    })
    public List<SimplifiedDebtResponse> getSimplifiedDebts(
            @CurrentUser UserPrincipal principal,
            @PathVariable("groupId") UUID groupId
    ) {
        return balanceService.getSimplifiedDebts(groupId);
    }
}
