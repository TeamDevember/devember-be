package com.gridians.gridians.global.error.exception;

public class CustomJwtException extends BusinessException{
    public CustomJwtException(String message) {
        super("jwt exception", ErrorCode.TOKEN_EXPIRE);
    }
}
