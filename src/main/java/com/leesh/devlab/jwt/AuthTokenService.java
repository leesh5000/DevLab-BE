package com.leesh.devlab.jwt;

import com.leesh.devlab.constant.TokenType;
import com.leesh.devlab.exception.ex.AuthException;
import com.leesh.devlab.jwt.dto.MemberInfo;
import com.leesh.devlab.jwt.implementation.JwtService;

import javax.naming.AuthenticationException;

/**
 * <p>권한 및 인증과 관련된 토큰을 생성하고 검증하는 역할을 하는 서비스를 추상화</p>
 * Implementation {@link JwtService}
 */
public interface AuthTokenService {

    /**
     * 토큰으로 부터 멤버 정보를 추출하는 메서드
     * @param accessToken
     * @return
     * @throws AuthException accessToken이 아니면 예외 발생
     */
    MemberInfo extractMemberInfo(String accessToken) throws AuthException;

    /**
     * 인증 토큰이 유효한지 검증하는 메서드
     * @param authToken 검증할 토큰
     * @param tokenType 검증할 토큰의 타입 {@link TokenType}
     * @throws AuthenticationException
     */
    void validateAuthToken(String authToken, TokenType tokenType) throws AuthException;

    /**
     * 유저 정보로 부터 토큰을 생성하는 메서드
     * @param memberInfo 토큰 생성을 위한 멤버 정보 {@link MemberInfo}
     * @param tokenType 생성하고자 하는 토큰 타입 {@link TokenType}
     * @return
     */
    String createAuthToken(MemberInfo memberInfo, TokenType tokenType);

}
