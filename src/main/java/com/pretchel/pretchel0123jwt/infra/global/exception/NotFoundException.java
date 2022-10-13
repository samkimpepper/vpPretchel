package com.pretchel.pretchel0123jwt.infra.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
public class NotFoundException extends RuntimeException{
    private String code;
    public NotFoundException() {
        super();
        code = "NOT_FOUND";
    }
}
