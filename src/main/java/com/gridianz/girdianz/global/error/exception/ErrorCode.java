package com.gridianz.girdianz.global.error.exception;

import lombok.Getter;
import lombok.Setter;

import javax.swing.text.html.Option;
import java.util.Optional;

@Getter
public enum ErrorCode {

    INVALID_INPUT_VALUE(400, "002", "invalid input value"),
    ENTITY_NOT_FOUND(400, "001", "Entity Not Found"),

    DUPLICATE_EMAIL(409, "A001", "Duplicate Email"),
    PASSWORD_NOT_MATCH(400, "A002", "Password Not Match"),
    EMAIL_NOT_VERIFIED(200, "A003", "email not verified"),
    DELETE_USER_ACCESS(400, "A004", "delete user access"),
    GIT_ID_NOT_FOUND(400, "A005", "git id not found");
    private int status;
    private String code;
    private String message;

    ErrorCode(int status, String code, String message){
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
