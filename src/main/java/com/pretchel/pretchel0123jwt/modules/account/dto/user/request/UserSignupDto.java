package com.pretchel.pretchel0123jwt.modules.account.dto.user.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSignupDto {
    private String email;
    private String password;
    private String checkPassword;
    private String birthday;
    private String gender;
    private String phoneNumber;
}
