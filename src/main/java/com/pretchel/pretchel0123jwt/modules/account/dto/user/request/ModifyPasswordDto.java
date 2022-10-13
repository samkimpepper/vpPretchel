package com.pretchel.pretchel0123jwt.modules.account.dto.user.request;

import lombok.Getter;

@Getter
public class ModifyPasswordDto {
    private String password;
    private String newPassword;
    private String checkPassword;
}
