package com.gridianz.girdianz.global.error.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    ENTITY_NOT_FOUND("400", "001", "Entity Not Found"),

    DUPLICATE_EMAIL("400", "A001", "Duplicate Email"),
    PASSWORD_NOT_MATCH("400", "A002", "Password Not Match"),
    EMAIL_NOT_VERIFIED("200", "A003", "email not verified"),
    DELETE_USER_ACCESS("400", "A004", "delete user access");
    private String status;
    private String code;
    private String message;

    ErrorCode(String status, String code, String message){
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
