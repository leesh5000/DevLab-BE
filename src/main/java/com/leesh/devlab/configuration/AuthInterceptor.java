package com.leesh.devlab.configuration;

import com.leesh.devlab.jwt.AuthTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthTokenService authTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {



        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
