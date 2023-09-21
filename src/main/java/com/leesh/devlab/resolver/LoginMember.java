package com.leesh.devlab.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *     현재 로그인 한 사용자의 정보를 담는 어노테이션 <br>
 *     {@link LoginMemberArgResolver#resolveArgument(MethodParameter, ModelAndViewContainer, NativeWebRequest, WebDataBinderFactory)}
 * </p>
 * 
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginMember {
}
