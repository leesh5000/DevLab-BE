package com.leesh.devlab.external.implementation.naver.dto;

import com.leesh.devlab.external.OauthToken;

import java.util.Objects;

public record NaverToken(String token_type, String access_token, Integer expires_in, String refresh_token, String error, String error_description) implements OauthToken {

    public NaverToken {
        Objects.requireNonNull(access_token, "NaverToken access_token must not be null");
    }

    @Override
    public String getAccessToken() {
        return access_token;
    }

}
