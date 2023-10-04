package com.leesh.devlab.jwt.implementation;

import com.leesh.devlab.exception.ErrorCode;
import com.leesh.devlab.domain.member.Role;
import com.leesh.devlab.jwt.TokenType;
import com.leesh.devlab.exception.custom.AuthException;
import com.leesh.devlab.jwt.Token;
import com.leesh.devlab.jwt.TokenService;
import com.leesh.devlab.jwt.dto.LoginInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Slf4j
@Service
public class JwtService implements TokenService {

    private final String tokenSecret;

    public JwtService(@Value("${token.secret}") String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    @Override
    public LoginInfo extractLoginInfo(String value) throws AuthException {

        Claims claims = extractAllClaims(value);

        // 접근 토큰이 아니면, 예외 던지기
        if (!TokenType.ACCESS.name().equals(claims.getSubject())) {
            throw new AuthException(ErrorCode.INVALID_TOKEN, "not access token");
        }

        return new LoginInfo(
                claims.get("id", Long.class),
                claims.get("nickname", String.class),
                Role.valueOf(claims.get("role", String.class))
        );
    }

    @Override
    public void validateToken(String value, TokenType tokenType) throws AuthException {

        Claims claims = extractAllClaims(value);

        // 토큰 타입 유효성 검증
        if (!tokenType.name().equals(claims.getSubject())) {
            throw new AuthException(ErrorCode.INVALID_TOKEN, "wrong token type");
        }
    }

    @Override
    public Token createToken(LoginInfo loginInfo, TokenType tokenType) {

        long expiredAt = System.currentTimeMillis() + tokenType.getExpiresInMills();
        String value;

        if (tokenType == TokenType.ACCESS) {

            value = Jwts.builder()
                    .setSubject(tokenType.name())              // 토큰 제목
                    .setIssuedAt(new Date())                   // 토큰 발급 시간
                    .setExpiration(new Date(expiredAt))        // 토큰 만료되는 시간
                    .claim("id", loginInfo.id())        // 회원 아이디 (PK값)
                    .claim("nickname", loginInfo.nickname())    // 회원 닉네임
                    .claim("role", loginInfo.role())    // 유저 role
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .setHeaderParam("typ", "JWT")
                    .compact();

        } else {

            value = Jwts.builder()
                    .setSubject(tokenType.name())              // 토큰 제목
                    .setIssuedAt(new Date())                   // 토큰 발급 시간
                    .setExpiration(new Date(expiredAt))        // 토큰 만료되는 시간
                    .claim("id", loginInfo.id())        // 회원 아이디 (PK값)
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .setHeaderParam("typ", "JWT")
                    .compact();
        }

        return new Jwt(tokenType, value, expiredAt);
    }

    // 이 메소드의 파라미터인 토큰은 해당 시점에서는 Access Token 또는 Refresh Token 인지 알 수 없다.
    private Claims extractAllClaims(String value) throws AuthException {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(value)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new AuthException(ErrorCode.EXPIRED_TOKEN, "expired token");
        } catch (Exception e) {
            throw new AuthException(ErrorCode.INVALID_TOKEN, "invalid token");
        }
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(tokenSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
