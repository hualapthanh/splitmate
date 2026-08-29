package com.pm.expenseservice.service.impl;

import com.pm.expenseservice.dto.request.CreateExpenseRequest;
import com.pm.expenseservice.dto.request.PayerRequest;
import com.pm.expenseservice.dto.request.SplitRequest;
import com.pm.expenseservice.dto.request.UpdateExpenseRequest;
import com.pm.expenseservice.dto.response.ExpenseDetailResponse;
import com.pm.expenseservice.dto.response.ExpenseResponse;
import com.pm.expenseservice.entity.Category;
import com.pm.expenseservice.entity.Expense;
import com.pm.expenseservice.entity.ExpensePayer;
import com.pm.expenseservice.entity.ExpenseSplit;
import com.pm.expenseservice.event.ExpenseCreatedEvent;
import com.pm.expenseservice.event.ExpenseDeletedEvent;
import com.pm.expenseservice.event.ExpenseEventPublisher;
import com.pm.expenseservice.event.ExpenseUpdatedEvent;
import com.pm.expenseservice.exception.BusinessException;
import com.pm.expenseservice.exception.ErrorCode;
import com.pm.expenseservice.exception.ResourceNotFoundException;
import com.pm.expenseservice.mapper.ExpenseMapper;
import com.pm.expenseservice.repository.CategoryRepository;
import com.pm.expenseservice.repository.ExpensePayerRepository;
import com.pm.expenseservice.repository.ExpenseRepository;
import com.pm.expenseservice.repository.ExpenseSplitRepository;
import com.pm.expenseservice.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpensePayerRepository expensePayerRepository;
    private final ExpenseSplitRepository expenseSplitRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseMapper expenseMapper;
    private final ExpenseEventPublisher expenseEventPublisher;

    @Override
    @Transactional
    public ExpenseDetailResponse createExpense(UUID userId, CreateExpenseRequest request) {
        log.info("Creating new expense '{}' of amount {} by userId: {}", request.getDescription(), request.getAmount(), userId);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.EXP_005, "Category not found with id: " + request.getCategoryId()));

        // Validate Payers Sum
        validatePayersSum(request.getAmount(), request.getPayers());

        Expense expense = expenseMapper.toExpense(request);
        expense.setUserId(userId);
        expense.setCategory(category);
        expense.setStatus("CONFIRMED");

        // Map Payers
        List<ExpensePayer> payers = request.getPayers().stream()
                .map(p -> ExpensePayer.builder()
                        .expense(expense)
                        .userId(p.getUserId())
                        .amountPaid(p.getAmountPaid())
                        .paymentMethod(p.getPaymentMethod() != null ? p.getPaymentMethod() : "CASH")
                        .build())
                .collect(Collectors.toList());
        expense.setPayers(payers);

        // Calculate and Map Splits
        List<ExpenseSplit> calculatedSplits = calculateSplits(expense, request.getAmount(), request.getSplitType(), request.getSplits());
        expense.setSplits(calculatedSplits);

        Expense savedExpense = expenseRepository.save(expense);

        // Publish Kafka Event
        publishExpenseCreatedEvent(savedExpense);

        log.info("Expense created successfully with expenseId: {}", savedExpense.getId());
        return expenseMapper.toExpenseDetailResponse(savedExpense);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseResponse> getUserExpenses(UUID userId) {
        List<Expense> expenses = expenseRepository.findAllAvailableForUser(userId);
        return expenses.stream()
                .map(expenseMapper::toExpenseResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseResponse> getGroupExpenses(UUID userId, UUID groupId) {
        List<Expense> expenses = expenseRepository.findAllByGroupId(groupId);
        return expenses.stream()
                .map(expenseMapper::toExpenseResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenseDetailResponse getExpenseDetails(UUID userId, UUID expenseId) {
        Expense expense = findExpenseByIdOrThrow(expenseId);
        return expenseMapper.toExpenseDetailResponse(expense);
    }

    @Override
    @Transactional
    public ExpenseDetailResponse updateExpense(UUID userId, UUID expenseId, UpdateExpenseRequest request) {
        Expense expense = findExpenseByIdOrThrow(expenseId);

        if (!expense.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.EXP_006, "Only the creator of the expense can update it");
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.EXP_005, "Category not found"));
            expense.setCategory(category);
        }

        expenseMapper.updateExpenseFromRequest(request, expense);

        BigDecimal effectiveAmount = request.getAmount() != null ? request.getAmount() : expense.getAmount();
        String effectiveSplitType = request.getSplitType() != null ? request.getSplitType() : (expense.getSplits().isEmpty() ? "EQUAL" : expense.getSplits().get(0).getSplitType());

        if (request.getPayers() != null && !request.getPayers().isEmpty()) {
            validatePayersSum(effectiveAmount, request.getPayers());
            expense.getPayers().clear();
            List<ExpensePayer> newPayers = request.getPayers().stream()
                    .map(p -> ExpensePayer.builder()
                            .expense(expense)
                            .userId(p.getUserId())
                            .amountPaid(p.getAmountPaid())
                            .paymentMethod(p.getPaymentMethod() != null ? p.getPaymentMethod() : "CASH")
                            .build())
                    .collect(Collectors.toList());
            expense.getPayers().addAll(newPayers);
        }

        if (request.getSplits() != null && !request.getSplits().isEmpty()) {
            expense.getSplits().clear();
            List<ExpenseSplit> newSplits = calculateSplits(expense, effectiveAmount, effectiveSplitType, request.getSplits());
            expense.getSplits().addAll(newSplits);
        }

        Expense updatedExpense = expenseRepository.save(expense);

        // Publish Kafka Event
        publishExpenseUpdatedEvent(updatedExpense);

        log.info("Expense updated successfully for expenseId: {}", expenseId);
        return expenseMapper.toExpenseDetailResponse(updatedExpense);
    }

    @Override
    @Transactional
    public void deleteExpense(UUID userId, UUID expenseId) {
        Expense expense = findExpenseByIdOrThrow(expenseId);

        if (!expense.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.EXP_006, "Only the creator of the expense can delete it");
        }

        expense.setStatus("DELETED");
        expenseRepository.save(expense);

        // Publish Kafka Event
        ExpenseDeletedEvent deletedEvent = ExpenseDeletedEvent.builder()
                .expenseId(expenseId)
                .userId(userId)
                .groupId(expense.getGroupId())
                .deletedAt(OffsetDateTime.now())
                .build();
        expenseEventPublisher.publishExpenseDeletedEvent(deletedEvent);

        log.info("Expense soft-deleted successfully for expenseId: {}", expenseId);
    }

    private Expense findExpenseByIdOrThrow(UUID expenseId) {
        return expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + expenseId));
    }

    private void validatePayersSum(BigDecimal totalAmount, List<PayerRequest> payers) {
        BigDecimal sumPaid = payers.stream()
                .map(PayerRequest::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (sumPaid.compareTo(totalAmount) != 0) {
            throw new BusinessException(ErrorCode.EXP_002,
                    String.format("Sum of amount_paid by payers (%s) does not match total expense amount (%s)", sumPaid, totalAmount));
        }
    }

    private List<ExpenseSplit> calculateSplits(Expense expense, BigDecimal totalAmount, String splitType, List<SplitRequest> splitRequests) {
        int participantCount = splitRequests.size();
        if (participantCount == 0) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Splits list cannot be empty");
        }

        List<ExpenseSplit> splits = new ArrayList<>();

        switch (splitType.toUpperCase()) {
            case "EQUAL":
                BigDecimal baseAmount = totalAmount.divide(BigDecimal.valueOf(participantCount), 2, RoundingMode.DOWN);
                BigDecimal totalCalculated = baseAmount.multiply(BigDecimal.valueOf(participantCount));
                BigDecimal remainder = totalAmount.subtract(totalCalculated);

                for (int i = 0; i < participantCount; i++) {
                    SplitRequest req = splitRequests.get(i);
                    BigDecimal participantAmount = (i == 0) ? baseAmount.add(remainder) : baseAmount;

                    ExpenseSplit split = ExpenseSplit.builder()
                            .expense(expense)
                            .userId(req.getUserId())
                            .splitType("EQUAL")
                            .amount(participantAmount)
                            .percentage(BigDecimal.ZERO)
                            .shares(BigDecimal.ZERO)
                            .build();
                    splits.add(split);
                }
                break;

            case "EXACT":
                BigDecimal sumExact = splitRequests.stream()
                        .map(s -> s.getAmount() != null ? s.getAmount() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                if (sumExact.compareTo(totalAmount) != 0) {
                    throw new BusinessException(ErrorCode.EXP_003,
                            String.format("Sum of exact split amounts (%s) does not equal total amount (%s)", sumExact, totalAmount));
                }

                for (SplitRequest req : splitRequests) {
                    ExpenseSplit split = ExpenseSplit.builder()
                            .expense(expense)
                            .userId(req.getUserId())
                            .splitType("EXACT")
                            .amount(req.getAmount())
                            .percentage(BigDecimal.ZERO)
                            .shares(BigDecimal.ZERO)
                            .build();
                    splits.add(split);
                }
                break;

            case "PERCENTAGE":
                BigDecimal sumPct = splitRequests.stream()
                        .map(s -> s.getPercentage() != null ? s.getPercentage() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                if (sumPct.compareTo(new BigDecimal("100.00")) != 0 && sumPct.compareTo(new BigDecimal("100")) != 0) {
                    throw new BusinessException(ErrorCode.EXP_004,
                            String.format("Sum of split percentages (%s) must equal exactly 100%%", sumPct));
                }

                BigDecimal calculatedPctTotal = BigDecimal.ZERO;
                for (int i = 0; i < participantCount; i++) {
                    SplitRequest req = splitRequests.get(i);
                    BigDecimal pct = req.getPercentage();
                    BigDecimal splitAmt = totalAmount.multiply(pct).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

                    splits.add(ExpenseSplit.builder()
                            .expense(expense)
                            .userId(req.getUserId())
                            .splitType("PERCENTAGE")
                            .amount(splitAmt)
                            .percentage(pct)
                            .shares(BigDecimal.ZERO)
                            .build());
                    calculatedPctTotal = calculatedPctTotal.add(splitAmt);
                }

                // Adjust rounding cents on first split if needed
                BigDecimal pctRemainder = totalAmount.subtract(calculatedPctTotal);
                if (pctRemainder.compareTo(BigDecimal.ZERO) != 0 && !splits.isEmpty()) {
                    splits.get(0).setAmount(splits.get(0).getAmount().add(pctRemainder));
                }
                break;

            case "SHARE":
                BigDecimal totalShares = splitRequests.stream()
                        .map(s -> s.getShares() != null ? s.getShares() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                if (totalShares.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Total shares must be greater than zero");
                }

                BigDecimal calculatedShareTotal = BigDecimal.ZERO;
                for (int i = 0; i < participantCount; i++) {
                    SplitRequest req = splitRequests.get(i);
                    BigDecimal shareVal = req.getShares();
                    BigDecimal splitAmt = totalAmount.multiply(shareVal).divide(totalShares, 2, RoundingMode.HALF_UP);

                    splits.add(ExpenseSplit.builder()
                            .expense(expense)
                            .userId(req.getUserId())
                            .splitType("SHARE")
                            .amount(splitAmt)
                            .percentage(BigDecimal.ZERO)
                            .shares(shareVal)
                            .build());
                    calculatedShareTotal = calculatedShareTotal.add(splitAmt);
                }

                BigDecimal shareRemainder = totalAmount.subtract(calculatedShareTotal);
                if (shareRemainder.compareTo(BigDecimal.ZERO) != 0 && !splits.isEmpty()) {
                    splits.get(0).setAmount(splits.get(0).getAmount().add(shareRemainder));
                }
                break;

            default:
                throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Unsupported split type: " + splitType);
        }

        return splits;
    }

    private void publishExpenseCreatedEvent(Expense expense) {
        List<ExpenseCreatedEvent.PayerItem> payers = expense.getPayers().stream()
                .map(p -> new ExpenseCreatedEvent.PayerItem(p.getUserId(), p.getAmountPaid()))
                .collect(Collectors.toList());

        List<ExpenseCreatedEvent.SplitItem> splits = expense.getSplits().stream()
                .map(s -> new ExpenseCreatedEvent.SplitItem(s.getUserId(), s.getSplitType(), s.getAmount()))
                .collect(Collectors.toList());

        ExpenseCreatedEvent event = ExpenseCreatedEvent.builder()
                .expenseId(expense.getId())
                .userId(expense.getUserId())
                .groupId(expense.getGroupId())
                .expenseType(expense.getExpenseType())
                .description(expense.getDescription())
                .amount(expense.getAmount())
                .date(expense.getDate())
                .payers(payers)
                .splits(splits)
                .createdAt(expense.getCreatedAt())
                .build();

        expenseEventPublisher.publishExpenseCreatedEvent(event);
    }

    private void publishExpenseUpdatedEvent(Expense expense) {
        List<ExpenseCreatedEvent.PayerItem> payers = expense.getPayers().stream()
                .map(p -> new ExpenseCreatedEvent.PayerItem(p.getUserId(), p.getAmountPaid()))
                .collect(Collectors.toList());

        List<ExpenseCreatedEvent.SplitItem> splits = expense.getSplits().stream()
                .map(s -> new ExpenseCreatedEvent.SplitItem(s.getUserId(), s.getSplitType(), s.getAmount()))
                .collect(Collectors.toList());

        ExpenseUpdatedEvent event = ExpenseUpdatedEvent.builder()
                .expenseId(expense.getId())
                .userId(expense.getUserId())
                .groupId(expense.getGroupId())
                .description(expense.getDescription())
                .amount(expense.getAmount())
                .date(expense.getDate())
                .payers(payers)
                .splits(splits)
                .updatedAt(expense.getUpdatedAt())
                .build();

        expenseEventPublisher.publishExpenseUpdatedEvent(event);
    }
}
