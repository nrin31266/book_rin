package com.rin.profile.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    INVALID_KEY(1001, "Uncategorized exception", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1004, "Password need at least {min} characters", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1007, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized exception", HttpStatus.INSUFFICIENT_STORAGE),
    USERNAME_INVALID(1003, "Username needs at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1006, "User not existed", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND(1005, "User not found", HttpStatus.NOT_FOUND),
    UNAUTHORIZED(1008, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1009, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    NOT_EMPTY(1010, "{value} must not be left empty", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN(1011, "{value} must not be left empty", HttpStatus.UNAUTHORIZED),
    EXPIRED(1012, "Token has expired", HttpStatus.UNAUTHORIZED),;
    private int code;
    private String message;
    private HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
