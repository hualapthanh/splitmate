package com.pm.budgetservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.budgetservice.dto.request.CreateBudgetRequest;
import com.pm.budgetservice.dto.response.BudgetResponse;
import com.pm.budgetservice.dto.response.BudgetSummaryResponse;
import com.pm.budgetservice.entity.BudgetScope;
import com.pm.budgetservice.exception.GlobalExceptionHandler;
import com.pm.budgetservice.security.UserPrincipal;
import com.pm.budgetservice.service.BudgetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BudgetControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private BudgetService budgetService;

    @InjectMocks
    private BudgetController budgetController;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        HandlerMethodArgumentResolver principalResolver = new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.hasParameterAnnotation(AuthenticationPrincipal.class)
                        && parameter.getParameterType().equals(UserPrincipal.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                          NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                return UserPrincipal.builder()
                        .userId(userId)
                        .email("user@example.com")
                        .role("USER")
                        .build();
            }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(budgetController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(principalResolver)
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/budgets - Create Budget")
    void createBudget_ReturnsCreated() throws Exception {
        CreateBudgetRequest request = CreateBudgetRequest.builder()
                .scope(BudgetScope.PERSONAL)
                .category("FOOD")
                .amountLimit(new BigDecimal("5000000.00"))
                .periodMonth("2026-09")
                .build();

        BudgetResponse response = BudgetResponse.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .scope(BudgetScope.PERSONAL)
                .category("FOOD")
                .amountLimit(new BigDecimal("5000000.00"))
                .currentSpent(BigDecimal.ZERO)
                .remainingAmount(new BigDecimal("5000000.00"))
                .percentageUsed(0.0)
                .status("NORMAL")
                .periodMonth("2026-09")
                .build();

        when(budgetService.createBudget(eq(userId), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/budgets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.category").value("FOOD"))
                .andExpect(jsonPath("$.amountLimit").value(5000000.00))
                .andExpect(jsonPath("$.status").value("NORMAL"));
    }

    @Test
    @DisplayName("GET /api/v1/budgets/summary - Get Budget Summary")
    void getBudgetSummary_ReturnsOk() throws Exception {
        BudgetSummaryResponse summary = BudgetSummaryResponse.builder()
                .periodMonth("2026-09")
                .totalBudgets(1)
                .totalLimitAmount(new BigDecimal("5000000.00"))
                .totalSpentAmount(new BigDecimal("1000000.00"))
                .overallPercentageUsed(20.0)
                .budgets(List.of())
                .build();

        when(budgetService.getBudgetSummary(eq(userId), any())).thenReturn(summary);

        mockMvc.perform(get("/api/v1/budgets/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.periodMonth").value("2026-09"))
                .andExpect(jsonPath("$.totalBudgets").value(1))
                .andExpect(jsonPath("$.overallPercentageUsed").value(20.0));
    }
}
