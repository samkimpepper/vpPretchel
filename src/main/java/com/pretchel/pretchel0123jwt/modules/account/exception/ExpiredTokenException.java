package com.pretchel.pretchel0123jwt.modules.account.exception;

import lombok.Getter;

@Getter
public class ExpiredTokenException extends RuntimeException {
    private String code;

    public ExpiredTokenException() {
        super();
        code = "EXPIRED_TOKEN";
    }
}
