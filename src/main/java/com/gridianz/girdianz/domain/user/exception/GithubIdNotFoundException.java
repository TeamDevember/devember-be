package com.gridianz.girdianz.domain.user.exception;

import com.gridianz.girdianz.global.error.exception.BusinessException;
import com.gridianz.girdianz.global.error.exception.BusinessExceptionWithParam;
import com.gridianz.girdianz.global.error.exception.ErrorCode;

public class GithubIdNotFoundException extends BusinessExceptionWithParam {
    public GithubIdNotFoundException(String message, String id) {
        super(message, ErrorCode.GIT_ID_NOT_FOUND, id);
    }
}
