package com.leesh.devlab.jwt.implementation;

import com.leesh.devlab.constant.ErrorCode;
import com.leesh.devlab.constant.Role;
import com.leesh.devlab.constant.TokenType;
import com.leesh.devlab.exception.ex.AuthException;
import com.leesh.devlab.jwt.AuthTokenService;
import com.leesh.devlab.jwt.dto.MemberInfo;
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
public class JwtService implements AuthTokenService {

    private final String tokenSecret;

    public JwtService(@Value("${token.secret}") String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    @Override
    public MemberInfo extractMemberInfo(String accessToken) throws AuthException {

        Claims claims = extractAllClaims(accessToken);

        // 접근 토큰이 아니면, 예외 던지기
        if (!TokenType.ACCESS.name().equals(claims.getSubject())) {
            throw new AuthException(ErrorCode.INVALID_TOKEN);
        }

        return new MemberInfo(
                claims.get("id", Long.class),
                claims.get("name", String.class),
                claims.get("email", String.class),
                claims.get("role", Role.class)
        );
    }

    @Override
    public void validateAuthToken(String value, TokenType tokenType) {

        Claims claims = extractAllClaims(value);

        // 토큰 타입 유효성 검증
        if (!tokenType.name().equals(claims.getSubject())) {
            throw new AuthException(ErrorCode.INVALID_TOKEN);
        }

    }

    @Override
    public String createAuthToken(MemberInfo memberInfo, TokenType tokenType) {

        Date expiredAt = new Date(System.currentTimeMillis() + tokenType.getExpiresIn());

        return Jwts.builder()
                .setSubject(tokenType.name())             // 토큰 제목
                .setIssuedAt(new Date())                  // 토큰 발급 시간
                .setExpiration(expiredAt)                 // 토큰 만료되는 시간
                .claim("id", memberInfo.id())        // 회원 아이디 (PK값)
                .claim("name", memberInfo.name())    // 회원 이름
                .claim("email", memberInfo.email())  // 회원 이메일
                .claim("role", memberInfo.role())    // 유저 role
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .setHeaderParam("typ", "JWT")
                .compact();
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
            throw new AuthException(ErrorCode.EXPIRED_TOKEN);
        } catch (Exception e) {
            throw new AuthException(ErrorCode.INVALID_TOKEN);
        }
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(tokenSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
