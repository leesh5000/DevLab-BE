package com.leesh.devlab.api.oauth.dto;

import com.leesh.devlab.api.oauth.validator.Email;
import com.leesh.devlab.api.oauth.validator.Nickname;
import com.leesh.devlab.constant.GrantType;
import com.leesh.devlab.constant.TokenType;
import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.jwt.AuthToken;

public class RegisterDto {

    protected RegisterDto() {
    }

    public record Request(@Email String email, String password, @Nickname String name) {

        public Member toEntity() {
            return Member.of(email(), password(), name());
        }

    }

    public record Response(GrantType grantType, AuthToken accessToken, AuthToken refreshToken) {

        public Response {

            if (grantType != GrantType.BEARER) {
                throw new IllegalArgumentException("grantType must be Bearer");
            }

            // 각 토큰 타입이 잘못된 경우
            if (accessToken.getTokenType() != TokenType.ACCESS || refreshToken.getTokenType() != TokenType.REFRESH) {
                throw new IllegalArgumentException("invalid token");
            }
        }
    }

}
