package com.pretchel.pretchel0123jwt.infra.global.exception;

import lombok.Getter;

@Getter
public class S3UploadException extends RuntimeException {
    private String code;

    public S3UploadException() {
        super();
        code = "S3_UPLOAD_FAIL";
    }
}
