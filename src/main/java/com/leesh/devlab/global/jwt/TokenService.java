package com.leesh.devlab.global.jwt;

import com.leesh.devlab.domain.member.Member;

import javax.naming.AuthenticationException;

public interface TokenService {

    MemberInfo extractMemberInfo(String accessToken) throws AuthenticationException;

    void validateAccessToken(String accessToken) throws AuthenticationException;

    void validateRefreshToken(String refreshToken) throws AuthenticationException;

    Jwt.AccessToken createAccessToken(Member member);

    Jwt.RefreshToken createRefreshToken(Long id);

}
