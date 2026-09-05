package com.pm.budgetservice.controller;

import com.pm.budgetservice.dto.request.CreateBudgetRequest;
import com.pm.budgetservice.dto.request.UpdateBudgetRequest;
import com.pm.budgetservice.dto.response.BudgetResponse;
import com.pm.budgetservice.dto.response.BudgetSummaryResponse;
import com.pm.budgetservice.security.UserPrincipal;
import com.pm.budgetservice.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateBudgetRequest request
    ) {
        BudgetResponse response = budgetService.createBudget(principal.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getUserBudgets(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(name = "periodMonth", required = false) String periodMonth
    ) {
        List<BudgetResponse> response = budgetService.getUserBudgets(principal.getUserId(), periodMonth);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/summary")
    public ResponseEntity<BudgetSummaryResponse> getBudgetSummary(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(name = "periodMonth", required = false) String periodMonth
    ) {
        BudgetSummaryResponse response = budgetService.getBudgetSummary(principal.getUserId(), periodMonth);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponse> getBudgetById(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("id") UUID id
    ) {
        BudgetResponse response = budgetService.getBudgetById(principal.getUserId(), id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponse> updateBudget(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateBudgetRequest request
    ) {
        BudgetResponse response = budgetService.updateBudget(principal.getUserId(), id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("id") UUID id
    ) {
        budgetService.deleteBudget(principal.getUserId(), id);
        return ResponseEntity.noContent().build();
    }
}
