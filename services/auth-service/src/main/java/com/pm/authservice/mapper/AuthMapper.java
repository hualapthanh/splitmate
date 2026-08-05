package com.pm.authservice.mapper;

import com.pm.authservice.dto.response.AccountResponse;
import com.pm.authservice.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    @Mapping(target = "status", source = "accountStatus")
    AccountResponse toAccountResponse(Account account);
}
