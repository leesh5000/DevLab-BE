package com.leesh.devlab.external.implementation.google.dto;

import com.leesh.devlab.external.OauthToken;

import java.util.Objects;

public record GoogleToken(String token_type, String access_token, Integer expires_in, String refresh_token, String scope, String id_token) implements OauthToken {

    public GoogleToken {
        Objects.requireNonNull(id_token, "GoogleToken id_token must not be null");
        Objects.requireNonNull(access_token, "GoogleToken access_token must not be null");
    }

    @Override
    public String getAccessToken() {
        return id_token;
    }

}
