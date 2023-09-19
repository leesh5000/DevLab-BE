package com.leesh.devlab.external.implementation.google.dto;

import com.leesh.devlab.external.abstraction.dto.OauthTokenRequest;
import com.leesh.devlab.external.abstraction.dto.OauthTokenResponse;
import lombok.Builder;
import lombok.Getter;

public class GoogleToken {

    @Getter
    @Builder
    public static class Request implements OauthTokenRequest {

        private String client_id;
        private String client_secret;
        private String code;
        private String grant_type;
        private String redirect_uri;
        private String code_verifier;

    }

    public record Response(String token_type, String access_token, Integer expires_in,
                           String refresh_token, String scope, String id_token) implements OauthTokenResponse {

        @Override
        public String getAccessToken() {
            return access_token;
        }

    }


}
