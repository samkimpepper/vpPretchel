package com.pretchel.pretchel0123jwt.modules.account.dto.user.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ModifyInfoDto {
    private String birthday;
    private String phoneNumber;
}
