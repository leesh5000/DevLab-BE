package com.leesh.devlab.jwt;

import com.leesh.devlab.constant.TokenType;
import com.leesh.devlab.jwt.implementation.Jwt;

/**
 * <p>
 *     인증을 위한 토큰을 추상화한 인터페이스로 구체적인 기술이 아닌 추상화에 의존하기 위함
 *     구현체는 {@link Jwt} 참고
 * </p>
 */
public interface AuthToken {

    TokenType getTokenType();

    String getValue();

}
