package com.leesh.devlab.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * <p>
 *     예외 발생 시, 클라이언트에게 전달할 에러 코드를 정의한다.<br>
 *     각 에러코드는 resources/messages 폴더 내에 있는 messages.properties 파일에 정의된 메세지와 매핑되어 클라이언트에게로 응답한다.
 * </p>
 */
@Getter
public enum ErrorCode {

    /* Auth */
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A-001"),
    INVALID_TOKEN(UNAUTHORIZED, "A-002"),
    INVALID_AUTHORIZATION_HEADER(UNAUTHORIZED, "A-003"),
    NOT_EXIST_AUTHORIZATION(UNAUTHORIZED, "A-004"),

    /* Oauth */
    NOT_SUPPORT_OAUTH_TYPE(HttpStatus.BAD_REQUEST, "O-001"),
    ALREADY_REGISTERED_FROM_KAKAO(CONFLICT, "O-002"),
    ALREADY_REGISTERED_FROM_GOOGLE(CONFLICT, "O-003"),
    ALREADY_REGISTERED_FROM_NAVER(CONFLICT, "O-004"),
    INVALID_OAUTH_TYPE(HttpStatus.BAD_REQUEST, "O-005"),

    /* Member */
    NOT_EXIST_MEMBER(HttpStatus.NOT_FOUND, "M-001"),
    ALREADY_REGISTERED_MEMBER(CONFLICT, "M-002"),

    /* Common */
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C-001"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "C-002"),
    ;

    private final HttpStatus httpStatus;
    private final String code;

    ErrorCode(HttpStatus httpStatus, String code) {
        this.httpStatus = httpStatus;
        this.code = code;
    }

/*
    /**
     * 앱이 실행될 때, properties에 있는 에러 메세지들을 주입시킨다.
     * FIXME : 이 코드는 애플리케이션 실행 시점에 에러 코드에 현재 로케일에 해당하는 메세지를 주입하기 때문에 추후 Locale이 바뀌더라도 메세지는 바뀌지 않음 -> GlobalExHandler에서 messageSourceAccessor를 주입받아서 직접 메세지를 가져오는 방식으로 변경
     *//*

    @Component
    public static class ErrorMessageInjector {
        private final MessageSourceAccessor messageSource;

        private ErrorMessageInjector(MessageSourceAccessor messageSource) {
            this.messageSource = messageSource;
        }

        @PostConstruct
        private void init() {
            for (ErrorCode errorCode : ErrorCode.values()) {
                errorCode.message = messageSource.getMessage(errorCode.code);
            }
        }
    }
*/

}
