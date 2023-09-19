package com.leesh.devlab.global.jwt;

import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.global.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.Date;

@Slf4j
@Service
public class JwtService implements TokenService {

    private final Long accessTokenValidTime;
    private final Long refreshTokenValidTime;
    private final String tokenSecret;

    public JwtService(@Value("${token.access-token-expiration-time}") Long accessTokenValidTime,
                      @Value("${token.refresh-token-expiration-time}") Long refreshTokenValidTime,
                      @Value("${token.secret}") String tokenSecret) {

        this.accessTokenValidTime = accessTokenValidTime;
        this.refreshTokenValidTime = refreshTokenValidTime;
        this.tokenSecret = tokenSecret;

    }

    @Override
    public MemberInfo extractMemberInfo(Jwt jwt) throws BusinessException {

        // Access Token 이 아니면, 예외 던지기

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

        Date expirationTime = new Date(System.currentTimeMillis() + accessTokenValidTime);

        return null;
    }

    @Override
    public Jwt.RefreshToken createRefreshToken(Long id) {
        return null;
    }
}
