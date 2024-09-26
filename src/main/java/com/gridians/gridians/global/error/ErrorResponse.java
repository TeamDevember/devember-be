package com.gridians.gridians.global.error;

import com.gridians.gridians.global.error.exception.ErrorCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class ErrorResponse {

    private String message;
    private int status;
    private String code;
    private String param;
    private List<FieldError> errors;

    private ErrorResponse(final ErrorCode errorCode) {
        this.message = errorCode.getMessage();
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.errors = new ArrayList<>();
    }

    private ErrorResponse(ErrorCode errorCode, String param) {
        this.message = errorCode.getMessage();
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.param = param;
        this.errors = new ArrayList<>();
    }

    private ErrorResponse(final ErrorCode errorCode, List<FieldError> errors) {
        this.message = errorCode.getMessage();
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.errors = errors;
    }

    public static ErrorResponse of(final ErrorCode errorCode) {
        return new ErrorResponse(errorCode);
    }

    public static ErrorResponse of(final ErrorCode errorCode, BindingResult bindingResult) {
        return new ErrorResponse(errorCode, FieldError.of(bindingResult));
    }

    public static ErrorResponse of(ErrorCode errorCode, String param) {
        return new ErrorResponse(errorCode, param);
    }

    public static class FieldError {
        private String field;
        private String value;
        private String reason;

        private FieldError(String field, String value, String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }

        public static List<FieldError> of(BindingResult bindingResult) {
            List<org.springframework.validation.FieldError> fieldErrors = bindingResult.getFieldErrors();
            return fieldErrors.stream().map((error) -> {
                        String field = error.getField();
                        String value = error.getRejectedValue() == null ? "" : error.getRejectedValue().toString();
                        String reason = error.getDefaultMessage();
                        return new FieldError(field, value, reason);
                    })
                    .collect(Collectors.toList());
        }
    }
}
