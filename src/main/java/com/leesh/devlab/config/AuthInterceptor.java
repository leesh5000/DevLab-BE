package com.leesh.devlab.config;

import com.leesh.devlab.jwt.TokenService;
import com.leesh.devlab.jwt.TokenType;
import com.leesh.devlab.jwt.dto.LoginInfo;
import com.leesh.devlab.util.HttpHeaderUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 1. Authorization Header 에서 액세스 토큰 추출 후 JWT 객체 생성
        String accessToken = HttpHeaderUtil.extractAuthorization(request);

        // 2. 올바른 토큰인지 검증
        tokenService.validateToken(accessToken, TokenType.ACCESS);

        // 3. 토큰으로부터 유저 정보 추출
        LoginInfo loginInfo = tokenService.extractLoginInfo(accessToken);

        // 4. 유저 정보를 Request에 저장 (LoginMemberArgResolver 에서 사용)
        request.setAttribute(LoginInfo.class.getName(), loginInfo);

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
