package com.pm.balanceservice.controller;

import com.pm.balanceservice.dto.response.GroupBalanceResponse;
import com.pm.balanceservice.dto.response.SimplifiedDebtResponse;
import com.pm.balanceservice.exception.GlobalExceptionHandler;
import com.pm.balanceservice.service.BalanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BalanceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BalanceService balanceService;

    @InjectMocks
    private BalanceController balanceController;

    private UUID sampleGroupId;
    private UUID sampleUserId;

    @BeforeEach
    void setUp() {
        sampleGroupId = UUID.randomUUID();
        sampleUserId = UUID.randomUUID();
        mockMvc = MockMvcBuilders.standaloneSetup(balanceController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/balances/group/{groupId}/simplified-debts should return simplified transactions")
    void getSimplifiedDebts_shouldReturn200() throws Exception {
        UUID debtor = UUID.randomUUID();
        UUID creditor = UUID.randomUUID();

        SimplifiedDebtResponse debt = SimplifiedDebtResponse.builder()
                .debtorId(debtor)
                .creditorId(creditor)
                .amount(new BigDecimal("100000.00"))
                .build();

        when(balanceService.getSimplifiedDebts(sampleGroupId)).thenReturn(Arrays.asList(debt));

        mockMvc.perform(get("/api/v1/balances/group/" + sampleGroupId + "/simplified-debts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(100000.00));
    }
}
