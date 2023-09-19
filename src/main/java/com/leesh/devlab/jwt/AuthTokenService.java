package com.leesh.devlab.jwt;

import com.leesh.devlab.constant.TokenType;
import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.exception.ex.AuthException;
import com.leesh.devlab.exception.ex.BusinessException;
import com.leesh.devlab.jwt.dto.MemberInfo;
import com.leesh.devlab.jwt.implementation.JwtService;

import javax.naming.AuthenticationException;

/**
 * <p>권한 및 인증과 관련된 토큰을 생성하고 검증하는 역할을 하는 서비스를 추상화</p>
 * Implementation {@link JwtService}
 */
public interface AuthTokenService {

    MemberInfo extractMemberInfo(AuthToken authToken) throws AuthException;

    /**
     * 인증 토큰이 유효한지 검증하는 메서드
     * @param authToken
     * @param tokenType 검증할 토큰의 타입
     * @throws AuthenticationException
     */
    void validateAuthToken(AuthToken authToken, TokenType tokenType) throws AuthException;

    AuthToken createAuthToken(Member member, TokenType tokenType);

}
