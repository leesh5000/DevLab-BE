package com.leesh.devlab.util;

import com.leesh.devlab.constant.ErrorCode;
import com.leesh.devlab.exception.ex.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import static com.leesh.devlab.constant.GrantType.isBearerType;

public class AuthHeaderUtils {

    private AuthHeaderUtils() {
    }

    public static String extractAuthorization(HttpServletRequest request) throws BusinessException {

        // Authorization 헤더가 없으면 예외 발생
        validateAuthorization(request);

        String[] authorizations = request.getHeader(HttpHeaders.AUTHORIZATION).split(" ");

        // Authorization 헤더가 Bearer 타입이 아니거나, 토큰이 없으면 예외 발생
        if (authorizations.length != 2) {

            // Bearer 타입이 아니면 예외 발생
            if (!isBearerType(authorizations[0])) {
                throw new BusinessException(ErrorCode.INVALID_AUTHORIZATION_HEADER);
            }

            // Bearer 타입은 맞는데, 토큰이 없는 경우
            throw new BusinessException(ErrorCode.NOT_EXIST_AUTHORIZATION);
        }

        return authorizations[1];
    }

    public static void validateAuthorization(HttpServletRequest request) {

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (!StringUtils.hasText(authorization)) {
            throw new BusinessException(ErrorCode.NOT_EXIST_AUTHORIZATION);
        }
    }

}
