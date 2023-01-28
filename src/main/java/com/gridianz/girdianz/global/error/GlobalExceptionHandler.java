package com.gridianz.girdianz.global.error;

import com.gridianz.girdianz.domain.user.exception.GithubIdNotFoundException;
import com.gridianz.girdianz.global.error.exception.BusinessException;
import com.gridianz.girdianz.global.error.exception.BusinessExceptionWithParam;
import com.gridianz.girdianz.global.error.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.info("handleMethodArgumentNotValidException", e);
        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, e.getBindingResult());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.info("handleEntityNotFoundException", e);
        final ErrorCode errorCode = e.getErrorCode();
        final ErrorResponse response = ErrorResponse.of(errorCode);
        return new ResponseEntity<>(response, HttpStatus.valueOf(errorCode.getStatus()));
    }

    @ExceptionHandler(BusinessExceptionWithParam.class)
    public ResponseEntity<ErrorResponse> handleBusinessExceptionWithParam(BusinessExceptionWithParam e) {
        log.info("handleBusinessExceptionWithParam", e);
        ErrorCode errorCode = e.getErrorCode();
        String param = e.getParam();
        ErrorResponse response = ErrorResponse.of(errorCode, param);
        return new ResponseEntity<>(response, HttpStatus.valueOf(errorCode.getStatus()));
    }
}
