package com.leesh.devlab.jwt;

import com.leesh.devlab.constant.TokenType;
import com.leesh.devlab.exception.ex.AuthException;
import com.leesh.devlab.jwt.dto.MemberInfo;
import com.leesh.devlab.jwt.implementation.JwtService;

/**
 * <p>권한 및 인증과 관련된 토큰을 생성하고 검증하는 역할을 하는 서비스를 추상화</p>
 * Implementation {@link JwtService}
 */
public interface AuthTokenService {

    /**
     * 토큰으로 부터 멤버 정보를 추출하는 메서드
     * @param tokenValue
     * @return
     * @throws AuthException accessToken이 아니면 예외 발생
     */
    MemberInfo extractMemberInfo(String tokenValue) throws AuthException;

    void validateAuthToken(String tokenValue, TokenType tokenType) throws AuthException;

    /**
     * 유저 정보로 부터 토큰을 생성하는 메서드
     * @param memberInfo 토큰 생성을 위한 멤버 정보 {@link MemberInfo}
     * @param tokenType 생성하고자 하는 토큰 타입 {@link TokenType}
     * @return
     */
    AuthToken createAuthToken(MemberInfo memberInfo, TokenType tokenType);

}
