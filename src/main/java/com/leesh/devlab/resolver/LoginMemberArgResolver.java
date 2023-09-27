package com.leesh.devlab.resolver;

import com.leesh.devlab.jwt.dto.MemberInfo;
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
        boolean hasMemberInfoClass = MemberInfo.class.isAssignableFrom(parameter.getParameterType());
        return hasLoginMemberAnnotation && hasMemberInfoClass;
    }

    /**
     * <p>
     *      {@link com.leesh.devlab.interceptor.AuthInterceptor}에서 담은 유저 정보를 추출
     * </p>
     * @param parameter the method parameter to resolve. This parameter must
     * have previously been passed to {@link #supportsParameter} which must
     * have returned {@code true}.
     * @param mavContainer the ModelAndViewContainer for the current request
     * @param webRequest the current request
     * @param binderFactory a factory for creating {@link WebDataBinder} instances
     * @return
     * @throws Exception
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        // 1. Request에서 유저 정보 추출
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        Object memberInfo = request.getAttribute(MemberInfo.class.getName());

        // 2. 유저 정보가 없으면 예외 발생
        if (memberInfo == null) {
            throw new IllegalStateException("no member info in request");
        }

        return memberInfo;
    }
}
