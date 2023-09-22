package com.leesh.devlab.external.implementation.kakao.dto;

import com.leesh.devlab.external.abstraction.OauthToken;

import java.util.Objects;

public record KakaoToken(String access_token, String token_type, String refresh_toke, int expires_in, String scope, int refresh_token_expires_in) implements OauthToken {

    public KakaoToken {
        Objects.requireNonNull(access_token, "KakaoToken access_token must not be null");
    }

    @Override
    public String getAccessToken() {
        return access_token;
    }

}
