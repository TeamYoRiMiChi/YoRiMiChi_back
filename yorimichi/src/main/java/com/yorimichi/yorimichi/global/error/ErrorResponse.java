package com.yorimichi.yorimichi.global.error;

import lombok.Getter;

@Getter
public class ErrorResponse {

    private final boolean success = false;
    private final String code;
    private final String message;

    public ErrorResponse(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }
}