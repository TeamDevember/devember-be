package com.gridians.gridians.global.error.exception;

import lombok.Getter;

@Getter
public class BusinessExceptionWithParam extends BusinessException{

    private String param;

    public BusinessExceptionWithParam(String message, ErrorCode errorCode, String param) {
        super(message, errorCode);
        this.param = param;
    }
}
