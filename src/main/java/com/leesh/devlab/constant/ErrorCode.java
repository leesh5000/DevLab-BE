package com.leesh.devlab.constant;

import com.leesh.devlab.exception.GlobalExHandler;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Getter
public enum ErrorCode {

    /* Auth */
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A-001"),
    INVALID_TOKEN(UNAUTHORIZED, "A-002"),
    INVALID_AUTHORIZATION_HEADER(UNAUTHORIZED, "A-003"),
    NOT_EXIST_AUTHORIZATION(UNAUTHORIZED, "A-004"),

    /* Member */
    NOT_EXIST_MEMBER(HttpStatus.NOT_FOUND, "M-001"),

    /* Common */
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C-001"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "C-002")

    ;

    private final HttpStatus httpStatus;

    /**
     * 이 필드를 통해 messages_properties 파일에서 해당하는 에러 메세지를 가져온다. <br>
     * {@link GlobalExHandler#messageSource}
     */
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
