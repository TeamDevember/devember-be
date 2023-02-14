package com.gridians.gridians.global.error.exception;

import lombok.Cleanup;
import lombok.Getter;

@Getter
public enum ErrorCode {

    INVALID_INPUT_VALUE(400, "002", "invalid input value"),
    ENTITY_NOT_FOUND(400, "001", "Entity Not Found"),

    DUPLICATE_EMAIL(409, "A001", "Duplicate Email"),
    DUPLICATE_NICKNAME(409, "A009", "Duplicate Nickname"),
    PASSWORD_NOT_MATCH(400, "A002", "Password Not Match"),
    EMAIL_NOT_VERIFIED(400, "A003", "email not verified"),
    DELETE_USER_ACCESS(400, "A004", "delete user access"),
    GIT_ID_NOT_FOUND(400, "A005", "git id not found"),
    IMAGE_NOT_FOUND(400, "A006", "image not found"),
    TOKEN_EXPIRE(401, "A007", "expire"),
    INVALID_TOKEN(401, "A008", "error");

    private int status;
    private String code;
    private String message;

    ErrorCode(int status, String code, String message){
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
