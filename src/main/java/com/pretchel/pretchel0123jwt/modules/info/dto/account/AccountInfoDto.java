package com.pretchel.pretchel0123jwt.modules.info.dto.account;

import com.pretchel.pretchel0123jwt.modules.info.domain.Account;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountInfoDto {
    private String name;
    private String accountNum;
    private String bank;
    private String bankCode;
    private String birthday;

    public static AccountInfoDto fromAccount(Account account) {
        return AccountInfoDto.builder()
                .name(account.getName())
                .accountNum(account.getAccountNum())
                .bank(account.getBank())
                .bankCode(account.getBankCode())
                .birthday(account.getBirthday())
                .build();
    }
}
