package com.pm.balanceservice.mapper;

import com.pm.balanceservice.dto.response.GroupBalanceResponse;
import com.pm.balanceservice.dto.response.SettlementResponse;
import com.pm.balanceservice.dto.response.UserDebtResponse;
import com.pm.balanceservice.entity.GroupBalance;
import com.pm.balanceservice.entity.Settlement;
import com.pm.balanceservice.entity.UserDebt;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BalanceMapper {

    GroupBalanceResponse toGroupBalanceResponse(GroupBalance groupBalance);

    List<GroupBalanceResponse> toGroupBalanceResponseList(List<GroupBalance> groupBalances);

    UserDebtResponse toUserDebtResponse(UserDebt userDebt);

    List<UserDebtResponse> toUserDebtResponseList(List<UserDebt> userDebts);

    SettlementResponse toSettlementResponse(Settlement settlement);

    List<SettlementResponse> toSettlementResponseList(List<Settlement> settlements);
}
