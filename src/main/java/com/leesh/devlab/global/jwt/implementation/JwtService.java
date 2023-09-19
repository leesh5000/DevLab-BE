package com.leesh.devlab.global.jwt.implementation;

import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.global.exception.BusinessException;
import com.leesh.devlab.global.exception.ErrorCode;
import com.leesh.devlab.global.jwt.AuthToken;
import com.leesh.devlab.global.jwt.AuthTokenService;
import com.leesh.devlab.global.jwt.MemberInfo;
import com.leesh.devlab.global.jwt.constant.TokenType;
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
    public MemberInfo extractMemberInfo(AuthToken authToken) throws BusinessException {
        return null;
    }

    @Override
    public void validateAuthToken(AuthToken authToken, TokenType tokenType) {

        Claims claims = extractAllClaims(authToken);

        // 토큰 타입 유효성 검증
        if (!tokenType.name().equals(claims.getSubject())) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

    }

    @Override
    public AuthToken createAuthToken(Member member, TokenType tokenType) {

        Date expiredAt = new Date(System.currentTimeMillis() + tokenType.getExpiresIn());

        String value = Jwts.builder()
                .setSubject(tokenType.name())             // 토큰 제목
                .setIssuedAt(new Date())                  // 토큰 발급 시간
                .setExpiration(expiredAt)                 // 토큰 만료되는 시간
                .claim("id", member.getId())        // 회원 아이디 (PK값)
                .claim("name", member.getName())    // 회원 이름
                .claim("email", member.getEmail())  // 회원 이메일
                .claim("role", member.getRole())    // 유저 role
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .setHeaderParam("typ", "JWT")
                .compact();

        return new Jwt(tokenType, value);
    }

    // 이 메소드의 파라미터인 토큰은 해당 시점에서는 Access Token 또는 Refresh Token 인지 알 수 없다.
    private Claims extractAllClaims(AuthToken authToken) throws BusinessException {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(authToken.getValue())
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new BusinessException(ErrorCode.EXPIRED_TOKEN);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(tokenSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
