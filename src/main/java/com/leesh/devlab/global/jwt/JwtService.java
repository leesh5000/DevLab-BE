package com.leesh.devlab.global.jwt;

import com.leesh.devlab.domain.member.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;

@Slf4j
@Service
public class JwtService implements TokenService {

    private final Long accessTokenExpirationTime;
    private final Long refreshTokenExpirationTime;
    private final String tokenSecret;

    public JwtService(@Value("${token.access-token-expiration-time}") Long accessTokenExpirationTime,
                      @Value("${token.refresh-token-expiration-time}") Long refreshTokenExpirationTime,
                      @Value("${token.secret}") String tokenSecret) {

        this.accessTokenExpirationTime = accessTokenExpirationTime;
        this.refreshTokenExpirationTime = refreshTokenExpirationTime;
        this.tokenSecret = tokenSecret;

    }

    @Override
    public MemberInfo extractMemberInfo(String accessToken) throws AuthenticationException {
        return null;
    }

    @Override
    public void validateAccessToken(String accessToken) throws AuthenticationException {

    }

    @Override
    public void validateRefreshToken(String refreshToken) throws AuthenticationException {

    }

    @Override
    public Jwt.AccessToken createAccessToken(Member member) {
        return null;
    }

    @Override
    public Jwt.RefreshToken createRefreshToken(Long id) {
        return null;
    }
}
