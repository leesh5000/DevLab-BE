package com.leesh.devlab.exception;

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
    ALREADY_REGISTERED_ID(CONFLICT, "M-002", "already registered Id"),
    ALREADY_REGISTERED_NICKNAME(CONFLICT, "M-003", "already registered nickname"),
    WRONG_PASSWORD(UNAUTHORIZED, "M-004", "Wrong Password"),
    NO_VERIFIED_EMAIL(UNAUTHORIZED, "M-005", "no verified email"),
    WRONG_CERT_NUMBER(UNAUTHORIZED, "M-006", "wrong certification number"),
    NO_PERMISSION(FORBIDDEN, "M-007", "no permission member."),

    /* Post */
    POST_SAVE_FAILED(INTERNAL_SERVER_ERROR, "P-001", "post save failed. please try again later."),
    EXCEED_HASHTAG_COUNT(BAD_REQUEST, "P-002", "post's hashtag count can't exceed 10."),

    /* Common */
    INVALID_INPUT(BAD_REQUEST, "C-001", "Bad Request"),
    EMAIL_SEND_FAILED(INTERNAL_SERVER_ERROR, "C-002", "Email Send Failed"),
    NOT_EXIST_RESOURCE(NOT_FOUND, "C-002", "not exist resource."),
    NOT_RESOURCE_OWNER(FORBIDDEN, "C-003", "not resource owner."),
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
