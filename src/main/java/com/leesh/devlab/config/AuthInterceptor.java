package com.leesh.devlab.config;

import com.leesh.devlab.constant.ErrorCode;
import com.leesh.devlab.constant.TokenType;
import com.leesh.devlab.constant.dto.LoginMemberDto;
import com.leesh.devlab.exception.custom.AuthException;
import com.leesh.devlab.jwt.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.leesh.devlab.constant.GrantType.isBearerType;

@RequiredArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 1. Authorization Header 에서 액세스 토큰 추출 후 JWT 객체 생성
        String accessToken = extractAuthorization(request);

        // 2. 올바른 토큰인지 검증
        tokenService.validateToken(accessToken, TokenType.ACCESS);

        // 3. 토큰으로부터 유저 정보 추출
        LoginMemberDto loginMemberDto = tokenService.extractLoginInfo(accessToken);

        // 4. 유저 정보를 Request에 저장 (LoginMemberArgResolver 에서 사용)
        request.setAttribute(LoginMemberDto.class.getName(), loginMemberDto);

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    private String extractAuthorization(HttpServletRequest request) throws AuthException {

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Authorization 헤더 검증
        validateAuthorization(authorization);

        return authorization.split(" ")[1];
    }

    private void validateAuthorization(String authorization) {

        // Authorization 헤더가 비어있으면 예외 발생
        if (!StringUtils.hasText(authorization)) {
            throw new AuthException(ErrorCode.NOT_EXIST_AUTHORIZATION, "Authorization header is empty");
        }

        String[] authorizations = authorization.split(" ");

        // Authorization 헤더가 Bearer 타입이 아니거나, 토큰이 없으면 예외 발생
        if (authorizations.length != 2) {

            // Bearer 타입이 아니면 예외 발생
            if (!isBearerType(authorizations[0])) {
                throw new AuthException(ErrorCode.INVALID_AUTHORIZATION_HEADER, "Invalid authorization type");
            }

            // Bearer 타입은 맞는데, 토큰이 없는 경우
            throw new AuthException(ErrorCode.NOT_EXIST_TOKEN, "access token is empty");
        }
    }
}
