package com.pretchel.pretchel0123jwt.global.exception;

import org.hibernate.cfg.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


public class BadRequestException extends RuntimeException{
    public BadRequestException(String msg) {
        super(msg);
    }

    public BadRequestException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
