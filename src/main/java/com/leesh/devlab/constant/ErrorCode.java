package com.leesh.devlab.constant;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

/**
 * 비즈니스 로직 중
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum ErrorCode {

    /* Auth */
    EXPIRED_TOKEN(UNAUTHORIZED, "A-001", "Expired Token"),
    INVALID_TOKEN(UNAUTHORIZED, "A-002", "Invalid Token"),
    INVALID_AUTHORIZATION_HEADER(UNAUTHORIZED, "A-003", "Invalid Authorization Header"),
    NOT_EXIST_AUTHORIZATION(UNAUTHORIZED, "A-004", "Authorization is empty"),
    NOT_EXIST_TOKEN(UNAUTHORIZED, "A-005", "Token is empty"),

    /* Member */
    NOT_EXIST_MEMBER(NOT_FOUND, "M-001", "Not Exist Member"),
    ALREADY_REGISTERED_MEMBER(CONFLICT, "M-002", "Already Registered Member"),
    WRONG_PASSWORD(UNAUTHORIZED, "M-003", "Wrong Password"),
    NO_VERIFIED_EMAIL(UNAUTHORIZED, "M-004", "no verified email"),
    WRONG_CERT_NUMBER(UNAUTHORIZED, "M-005", "wrong certification number"),
    NO_PERMISSION(FORBIDDEN, "M-006", "no permission member."),

    /* Post */
    POST_SAVE_FAILED(INTERNAL_SERVER_ERROR, "P-001", "post save failed. please try again later."),
    NOT_EXIST_POST(NOT_FOUND, "P-002", "not exist post"),
    EXCEED_TAG_COUNT(BAD_REQUEST, "P-002", "tag count must be less than 10"),
    NOT_POST_AUTHOR(FORBIDDEN, "P-003", "only post's author can edit."),

    /* Common */
    INVALID_INPUT(BAD_REQUEST, "C-001", "Bad Request"),
    EMAIL_SEND_FAILED(INTERNAL_SERVER_ERROR, "C-002", "Email Send Failed"),
    ;

    private final HttpStatus status;
    private final String code;
    private final String description;

    ErrorCode(HttpStatus status, String code, String description) {
        this.status = status;
        this.code = code;
        this.description = description;
    }

}
