package com.leesh.devlab.constant;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum ErrorCode {

    /* Auth */
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A-001", "Expired Token"),
    INVALID_TOKEN(UNAUTHORIZED, "A-002", "Invalid Token"),
    INVALID_AUTHORIZATION_HEADER(UNAUTHORIZED, "A-003", "Invalid Authorization Header"),
    NOT_EXIST_AUTHORIZATION(UNAUTHORIZED, "A-004", "Authorization is empty"),

    /* Member */
    NOT_EXIST_MEMBER(HttpStatus.NOT_FOUND, "M-001", "Not Exist Member"),
    ALREADY_REGISTERED_MEMBER(CONFLICT, "M-002", "Already Registered Member"),
    ;

    private final HttpStatus status;
    private final Error error;

    ErrorCode(HttpStatus status, String code, String description) {
        this.status = status;
        this.error = new Error(code, description);
    }

    private record Error(String code, String message) {
    }

}
