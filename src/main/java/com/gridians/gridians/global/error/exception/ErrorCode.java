package com.gridians.gridians.global.error.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    INVALID_INPUT_VALUE(400, "002", "Invalid input value"),
    ENTITY_NOT_FOUND(400, "001", "Entity not found"),
    DUPLICATE_EMAIL(409, "A001", "Duplicated email"),
    PASSWORD_NOT_MATCH(400, "A002", "Password not match"),
    EMAIL_NOT_VERIFIED(200, "A003", "Email not verified"),
    DELETE_USER_ACCESS(400, "A004", "Delete user access"),
    GIT_ID_NOT_FOUND(400, "A005", "Git id not found"),
    IMAGE_NOT_FOUND(400, "A006", "ProfileImage not found"),
    TOKEN_EXPIRE(401, "A007", "Expire"),
    INVALID_TOKEN(401, "A008", "Error"),
    USER_NOT_FOUND(400, "A009" ,"User not found "),
    DUPLICATED_USER(400, "A010", "Duplicated user"),
    DUPLICATED_NICKNAME(400, "A011", "Duplicated nickname"),
    DUPLICATED_FAVORITE_USER(400, "A022", "Duplicated favorite user"),
    WRONG_USER_PASSWORD(400, "A012", "Wrong password"),
    OVERLAP_STATUS(400, "A013", "Overlap status"),
    NOT_AUTHENTICATED(400, "A014", "Not authenticated account"),
    UPROAD_ONLY_IMAGE_FILE(400, "A015", "Upload only image file"),
    CARD_NOT_FOUND(400, "A016", "Card not found"),
    DUPLICATED_SKILL(400, "A017", "Duplicated skill"),
    COMMENT_NOT_FOUND(400, "A018", "Comment not found"),
    REPLY_NOT_FOUND(400, "A019", "Reply not found"),
    MODIFY_ONLY_WRITER(400, "A020", "Modify only writer"),
    DELETE_ONLY_WRITER(400, "A021", "Delete only writer"),
    DUPLICATED_EMAIL(400, "A022", "Duplicated email"),
    FAVORITE_USER_NOT_FOUND(400, "A022", "Favorite user not found");



    private int status;
    private String code;
    private String message;

    ErrorCode(int status, String code, String message){
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
