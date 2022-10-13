package com.pretchel.pretchel0123jwt.modules.info.dto.account;

import com.pretchel.pretchel0123jwt.modules.info.domain.Account;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountListDto {
    private String id;
    private String name;
    private String accountNum;
    private String bank;
    private String bankCode;
    private String birthday;

    public static AccountListDto fromAccount(Account account) {
        return AccountListDto.builder()
                .id(account.getId())
                .name(account.getName())
                .accountNum(account.getAccountNum())
                .bank(account.getBank())
                .bankCode(account.getBankCode())
                .birthday(account.getBirthday())
                .build();
    }
}
