package com.pm.expenseservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pm.expenseservice.dto.request.CreateExpenseRequest;
import com.pm.expenseservice.dto.request.PayerRequest;
import com.pm.expenseservice.dto.request.SplitRequest;
import com.pm.expenseservice.dto.response.ExpenseDetailResponse;
import com.pm.expenseservice.exception.GlobalExceptionHandler;
import com.pm.expenseservice.service.ExpenseService;
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
import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ExpenseControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    private ExpenseService expenseService;

    @InjectMocks
    private ExpenseController expenseController;

    private UUID sampleUserId;
    private UUID sampleCategoryId;

    @BeforeEach
    void setUp() {
        sampleUserId = UUID.randomUUID();
        sampleCategoryId = UUID.randomUUID();
        mockMvc = MockMvcBuilders.standaloneSetup(expenseController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/expenses should create expense and return 201 Created")
    void createExpense_shouldReturn201() throws Exception {
        CreateExpenseRequest request = CreateExpenseRequest.builder()
                .expenseType("GROUP")
                .description("Seafood Dinner")
                .amount(new BigDecimal("600000.00"))
                .categoryId(sampleCategoryId)
                .date(LocalDate.now())
                .splitType("EQUAL")
                .payers(Arrays.asList(
                        PayerRequest.builder().userId(sampleUserId).amountPaid(new BigDecimal("600000.00")).paymentMethod("CASH").build()
                ))
                .splits(Arrays.asList(
                        SplitRequest.builder().userId(sampleUserId).build()
                ))
                .build();

        ExpenseDetailResponse response = ExpenseDetailResponse.builder()
                .id(UUID.randomUUID())
                .description("Seafood Dinner")
                .amount(new BigDecimal("600000.00"))
                .build();

        when(expenseService.createExpense(any(), any(CreateExpenseRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Seafood Dinner"));
    }
}
