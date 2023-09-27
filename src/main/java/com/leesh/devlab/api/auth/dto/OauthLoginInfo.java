package com.leesh.devlab.api.auth.dto;

import com.leesh.devlab.constant.GrantType;
import com.leesh.devlab.constant.OauthType;
import com.leesh.devlab.constant.TokenType;
import com.leesh.devlab.jwt.AuthToken;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OauthLoginInfo {

    public record Request(OauthType oauthType, String authorizationCode) {
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
