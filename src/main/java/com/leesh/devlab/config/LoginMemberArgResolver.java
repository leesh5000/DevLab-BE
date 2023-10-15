package com.leesh.devlab.config;

import com.leesh.devlab.jwt.dto.LoginInfo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class LoginMemberArgResolver implements HandlerMethodArgumentResolver {


    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        boolean hasLoginMemberAnnotation = parameter.hasParameterAnnotation(LoginMember.class);
        boolean hasMemberInfoClass = LoginInfo.class.isAssignableFrom(parameter.getParameterType());
        return hasLoginMemberAnnotation && hasMemberInfoClass;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        // 1. Request에서 유저 정보 추출
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        Object memberInfo = request.getAttribute(LoginInfo.class.getName());

        // 2. 유저 정보가 없으면 예외 발생
        if (memberInfo == null) {
            throw new IllegalStateException("no member info in request");
        }

        return memberInfo;
    }
}
