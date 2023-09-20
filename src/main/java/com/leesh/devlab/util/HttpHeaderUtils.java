package com.leesh.devlab.util;

import com.leesh.devlab.constant.ErrorCode;
import com.leesh.devlab.exception.ex.BusinessException;
import org.springframework.util.StringUtils;

import static com.leesh.devlab.constant.GrantType.isBearerType;

public class HttpHeaderUtils {

    // 인스턴스화 방지
    private HttpHeaderUtils() {
    }

    public static String extractToken(String authorization) throws BusinessException {

        // Authorization 헤더 검증
        validateAuthorization(authorization);

        return authorization.split(" ")[1];
    }

    public static void validateAuthorization(String authorization) {

        // Authorization 헤더가 비어있으면 예외 발생
        if (!StringUtils.hasText(authorization)) {
            throw new BusinessException(ErrorCode.NOT_EXIST_AUTHORIZATION);
        }

        String[] authorizations =authorization.split(" ");

        // Authorization 헤더가 Bearer 타입이 아니거나, 토큰이 없으면 예외 발생
        if (authorizations.length != 2) {

            // Bearer 타입이 아니면 예외 발생
            if (!isBearerType(authorizations[0])) {
                throw new BusinessException(ErrorCode.INVALID_AUTHORIZATION_HEADER);
            }

            // Bearer 타입은 맞는데, 토큰이 없는 경우
            throw new BusinessException(ErrorCode.NOT_EXIST_AUTHORIZATION);
        }
    }

}
