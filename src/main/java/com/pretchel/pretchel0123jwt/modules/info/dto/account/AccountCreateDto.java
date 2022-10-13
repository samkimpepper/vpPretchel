package com.pretchel.pretchel0123jwt.modules.info.dto.account;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountCreateDto {
    private String name;
    private String accountNum;
    private String bank;
    private String bankCode;
    private String birthday;
    private Boolean isDefault;
}
