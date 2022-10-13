package com.pretchel.pretchel0123jwt.infra.global.exception;

import lombok.Getter;

@Getter
public class EmptyValueExistsException extends RuntimeException{
    private String code;

    public EmptyValueExistsException() {
        super();
        code = "EMPTY_VALUE";
    }
}
