package com.pm.expenseservice.service;

import com.pm.expenseservice.dto.request.CreateExpenseRequest;
import com.pm.expenseservice.dto.request.PayerRequest;
import com.pm.expenseservice.dto.request.SplitRequest;
import com.pm.expenseservice.dto.response.ExpenseDetailResponse;
import com.pm.expenseservice.entity.Category;
import com.pm.expenseservice.entity.Expense;
import com.pm.expenseservice.event.ExpenseEventPublisher;
import com.pm.expenseservice.exception.BusinessException;
import com.pm.expenseservice.mapper.ExpenseMapper;
import com.pm.expenseservice.repository.CategoryRepository;
import com.pm.expenseservice.repository.ExpensePayerRepository;
import com.pm.expenseservice.repository.ExpenseRepository;
import com.pm.expenseservice.repository.ExpenseSplitRepository;
import com.pm.expenseservice.service.impl.ExpenseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private ExpensePayerRepository expensePayerRepository;

    @Mock
    private ExpenseSplitRepository expenseSplitRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ExpenseMapper expenseMapper;

    @Mock
    private ExpenseEventPublisher expenseEventPublisher;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    private UUID sampleUserId;
    private UUID sampleCategoryId;
    private UUID sampleGroupId;
    private Category sampleCategory;

    @BeforeEach
    void setUp() {
        sampleUserId = UUID.randomUUID();
        sampleCategoryId = UUID.randomUUID();
        sampleGroupId = UUID.randomUUID();

        sampleCategory = Category.builder()
                .id(sampleCategoryId)
                .name("Food & Dining")
                .icon("🍽️")
                .build();
    }

    @Test
    @DisplayName("createExpense() should create expense with EQUAL split and publish Kafka event")
    void createExpense_equalSplit_success() {
        UUID userB = UUID.randomUUID();
        UUID userC = UUID.randomUUID();

        CreateExpenseRequest request = CreateExpenseRequest.builder()
                .groupId(sampleGroupId)
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
                        SplitRequest.builder().userId(sampleUserId).build(),
                        SplitRequest.builder().userId(userB).build(),
                        SplitRequest.builder().userId(userC).build()
                ))
                .build();

        Expense expense = Expense.builder()
                .id(UUID.randomUUID())
                .userId(sampleUserId)
                .description("Seafood Dinner")
                .amount(new BigDecimal("600000.00"))
                .payers(new ArrayList<>())
                .splits(new ArrayList<>())
                .build();

        ExpenseDetailResponse expectedResponse = ExpenseDetailResponse.builder()
                .id(expense.getId())
                .description("Seafood Dinner")
                .amount(new BigDecimal("600000.00"))
                .build();

        when(categoryRepository.findById(sampleCategoryId)).thenReturn(Optional.of(sampleCategory));
        when(expenseMapper.toExpense(request)).thenReturn(expense);
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);
        when(expenseMapper.toExpenseDetailResponse(expense)).thenReturn(expectedResponse);

        ExpenseDetailResponse response = expenseService.createExpense(sampleUserId, request);

        assertNotNull(response);
        assertEquals("Seafood Dinner", response.getDescription());
        verify(expenseRepository).save(any(Expense.class));
        verify(expenseEventPublisher).publishExpenseCreatedEvent(any());
    }

    @Test
    @DisplayName("createExpense() should throw BusinessException when sum of payers does not match total amount")
    void createExpense_invalidPayersSum_throwsException() {
        CreateExpenseRequest request = CreateExpenseRequest.builder()
                .amount(new BigDecimal("600000.00"))
                .categoryId(sampleCategoryId)
                .payers(Arrays.asList(
                        PayerRequest.builder().userId(sampleUserId).amountPaid(new BigDecimal("400000.00")).build()
                ))
                .splits(Arrays.asList(
                        SplitRequest.builder().userId(sampleUserId).build()
                ))
                .build();

        when(categoryRepository.findById(sampleCategoryId)).thenReturn(Optional.of(sampleCategory));

        assertThrows(BusinessException.class, () -> expenseService.createExpense(sampleUserId, request));
    }

    @Test
    @DisplayName("createExpense() should throw BusinessException when sum of percentages != 100%")
    void createExpense_invalidPercentages_throwsException() {
        UUID userB = UUID.randomUUID();

        CreateExpenseRequest request = CreateExpenseRequest.builder()
                .amount(new BigDecimal("1000.00"))
                .categoryId(sampleCategoryId)
                .splitType("PERCENTAGE")
                .payers(Arrays.asList(
                        PayerRequest.builder().userId(sampleUserId).amountPaid(new BigDecimal("1000.00")).build()
                ))
                .splits(Arrays.asList(
                        SplitRequest.builder().userId(sampleUserId).percentage(new BigDecimal("40.00")).build(),
                        SplitRequest.builder().userId(userB).percentage(new BigDecimal("50.00")).build() // Total 90%
                ))
                .build();

        when(categoryRepository.findById(sampleCategoryId)).thenReturn(Optional.of(sampleCategory));
        when(expenseMapper.toExpense(request)).thenReturn(Expense.builder().payers(new ArrayList<>()).build());

        assertThrows(BusinessException.class, () -> expenseService.createExpense(sampleUserId, request));
    }
}
