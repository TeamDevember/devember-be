package com.gridians.gridians.global.error.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    INVALID_INPUT_VALUE(400, "A001", "Invalid input value"),
    NOT_AUTHENTICATED(400, "A002", "Not authenticated account"),
    EMAIL_NOT_VERIFIED(200, "A003", "Email not verified"),

    ENTITY_NOT_FOUND(400, "A004", "Entity not found"),
    GIT_ID_NOT_FOUND(400, "A005", "Git id not found"),
    IMAGE_NOT_FOUND(400, "A006", "ProfileImage not found"),
    USER_NOT_FOUND(400, "A007" ,"User not found "),
    CARD_NOT_FOUND(400, "A008", "Card not found"),
    COMMENT_NOT_FOUND(400, "A009", "Comment not found"),
    REPLY_NOT_FOUND(400, "A010", "Reply not found"),
    GITHUB_NOT_FOUND(400, "A011", "Github not found"),
    FAVORITE_USER_NOT_FOUND(400, "A012", "Favorite user not found"),
    FIELD_NOT_FOUND(400, "A013", "Favorite user not found"),
    SKILL_NOT_FOUND(400, "A014", "Favorite user not found"),

    DUPLICATE_EMAIL(409, "A015", "Duplicated email"),
    DUPLICATED_USER(409, "A016", "Duplicated user"),
    DUPLICATED_NICKNAME(409, "A017", "Duplicated nickname"),
    DUPLICATED_FAVORITE_USER(409, "A018", "Duplicated favorite user"),
    DUPLICATED_SKILL(409, "A019", "Duplicated skill"),
    DUPLICATED_EMAIL(409, "A020", "Duplicated email"),
    DUPLICATED_GITHUB_ID(409, "A021", "Duplicated github ID"),

    MODIFY_ONLY_OWNER(400, "A022", "Modify only owner"),
    DELETE_ONLY_OWNER(400, "A023", "Delete only owner"),
    MODIFY_ONLY_WRITER(400, "A024", "Modify only writer"),
    DELETE_ONLY_WRITER(400, "A025", "Delete only writer"),
    UPROAD_ONLY_IMAGE_FILE(400, "A026", "Upload only image file"),

    WRONG_USER_PASSWORD(400, "A027", "Wrong password"),
    DELETE_USER_ACCESS(400, "A028", "Delete user access"),

    TOKEN_EXPIRE(401, "A029", "Expire"),
    INVALID_TOKEN(401, "A030", "Error"),

    ALREADY_STATUS(400, "A031", "Already status"),
    DO_NOT_ADD_YOURSELF(400, "A032", "Do not add yourslef");






    private int status;
    private String code;
    private String message;

    ErrorCode(int status, String code, String message){
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
