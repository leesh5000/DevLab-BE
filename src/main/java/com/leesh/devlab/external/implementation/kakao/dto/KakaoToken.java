package com.leesh.devlab.external.implementation.kakao.dto;

import com.leesh.devlab.external.OauthTokenRequest;
import com.leesh.devlab.external.OauthTokenResponse;
import lombok.Builder;
import lombok.Getter;

public class KakaoToken {

    @Getter
    @Builder
    public static class Request implements OauthTokenRequest {

        private String grant_type;
        private String client_id;
        private String redirect_uri;
        private String code;
        private String client_secret;
        private String state;

    }

    public record Response(
            String access_token, String token_type, String refresh_token,
            int expires_in, String scope, int refresh_token_expires_in
    ) implements OauthTokenResponse {
        @Override
        public String getAccessToken() {
            return access_token;
        }
    }

}
