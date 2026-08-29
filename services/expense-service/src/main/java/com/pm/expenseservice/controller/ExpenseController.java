package com.pm.expenseservice.controller;

import com.pm.expenseservice.dto.request.CreateExpenseRequest;
import com.pm.expenseservice.dto.request.UpdateExpenseRequest;
import com.pm.expenseservice.dto.response.ExpenseDetailResponse;
import com.pm.expenseservice.dto.response.ExpenseResponse;
import com.pm.expenseservice.security.UserPrincipal;
import com.pm.expenseservice.security.annotation.CurrentUser;
import com.pm.expenseservice.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
@Tag(name = "Expense Management", description = "Endpoints for creating, splitting, retrieving, updating, and deleting expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new expense", description = "Creates a personal or group expense with multi-payers and split calculations (EQUAL, EXACT, PERCENTAGE, SHARE).")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Expense created successfully"),
        @ApiResponse(responseCode = "400", description = "Validation error or invalid payers/splits calculation sum"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ExpenseDetailResponse createExpense(
            @CurrentUser UserPrincipal principal,
            @Valid @RequestBody CreateExpenseRequest request
    ) {
        return expenseService.createExpense(principal.getUserId(), request);
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get user's expenses", description = "Returns all personal and group expenses involving the authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Expenses retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public List<ExpenseResponse> getUserExpenses(@CurrentUser UserPrincipal principal) {
        return expenseService.getUserExpenses(principal.getUserId());
    }

    @GetMapping("/group/{groupId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get group expenses", description = "Returns all active group expenses for a specific group.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Group expenses retrieved successfully")
    })
    public List<ExpenseResponse> getGroupExpenses(
            @CurrentUser UserPrincipal principal,
            @PathVariable("groupId") UUID groupId
    ) {
        return expenseService.getGroupExpenses(principal.getUserId(), groupId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get expense details", description = "Returns full breakdown of an expense including payers and split participants.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Expense details retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Expense not found")
    })
    public ExpenseDetailResponse getExpenseDetails(
            @CurrentUser UserPrincipal principal,
            @PathVariable("id") UUID expenseId
    ) {
        return expenseService.getExpenseDetails(principal.getUserId(), expenseId);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update expense", description = "Updates description, amount, category, date, payers, or splits of an expense.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Expense updated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied. Only creator can update"),
        @ApiResponse(responseCode = "404", description = "Expense not found")
    })
    public ExpenseDetailResponse updateExpense(
            @CurrentUser UserPrincipal principal,
            @PathVariable("id") UUID expenseId,
            @Valid @RequestBody UpdateExpenseRequest request
    ) {
        return expenseService.updateExpense(principal.getUserId(), expenseId, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete expense", description = "Soft deletes an expense and notifies downstream services via Kafka.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Expense deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied. Only creator can delete"),
        @ApiResponse(responseCode = "404", description = "Expense not found")
    })
    public void deleteExpense(
            @CurrentUser UserPrincipal principal,
            @PathVariable("id") UUID expenseId
    ) {
        expenseService.deleteExpense(principal.getUserId(), expenseId);
    }
}
