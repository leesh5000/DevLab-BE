package com.leesh.devlab.global.jwt;

import com.leesh.devlab.global.jwt.constant.TokenType;

/**
 * <p>
 *     인증을 위한 토큰을 추상화한 인터페이스로 구체적인 기술이 아닌 추상화에 의존하기 위함
 *     구현체는 {@link com.leesh.devlab.global.jwt.implementation.Jwt} 참고
 * </p>
 */
public interface AuthToken {

    TokenType getTokenType();

    String getValue();

}
