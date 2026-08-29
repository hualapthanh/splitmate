package com.pm.expenseservice.mapper;

import com.pm.expenseservice.dto.request.CreateExpenseRequest;
import com.pm.expenseservice.dto.request.UpdateExpenseRequest;
import com.pm.expenseservice.dto.response.ExpenseDetailResponse;
import com.pm.expenseservice.dto.response.ExpenseResponse;
import com.pm.expenseservice.dto.response.PayerResponse;
import com.pm.expenseservice.dto.response.SplitResponse;
import com.pm.expenseservice.entity.Expense;
import com.pm.expenseservice.entity.ExpensePayer;
import com.pm.expenseservice.entity.ExpenseSplit;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface ExpenseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "payers", ignore = true)
    @Mapping(target = "splits", ignore = true)
    Expense toExpense(CreateExpenseRequest request);

    ExpenseResponse toExpenseResponse(Expense expense);

    ExpenseDetailResponse toExpenseDetailResponse(Expense expense);

    @Mapping(target = "expenseId", source = "expense.id")
    PayerResponse toPayerResponse(ExpensePayer payer);

    List<PayerResponse> toPayerResponseList(List<ExpensePayer> payers);

    @Mapping(target = "expenseId", source = "expense.id")
    SplitResponse toSplitResponse(ExpenseSplit split);

    List<SplitResponse> toSplitResponseList(List<ExpenseSplit> splits);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "payers", ignore = true)
    @Mapping(target = "splits", ignore = true)
    void updateExpenseFromRequest(UpdateExpenseRequest request, @MappingTarget Expense expense);
}
