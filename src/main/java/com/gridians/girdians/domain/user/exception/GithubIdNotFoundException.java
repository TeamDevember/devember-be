package com.gridians.girdians.domain.user.exception;

import com.gridians.girdians.global.error.exception.BusinessExceptionWithParam;
import com.gridians.girdians.global.error.exception.ErrorCode;

public class GithubIdNotFoundException extends BusinessExceptionWithParam {
    public GithubIdNotFoundException(String message, String id) {
        super(message, ErrorCode.GIT_ID_NOT_FOUND, id);
    }
}
