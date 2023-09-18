package com.leesh.devlab.external.implementation.naver.dto;


import com.leesh.devlab.external.OauthTokenRequest;
import com.leesh.devlab.external.OauthTokenResponse;
import lombok.Builder;
import lombok.Getter;

public class NaverToken {

    /**
     * Record 클래스를 @SpringQueryMap 과 사용 시, 데이터를 파싱하지 못하는 버그로 인해 Class 사용
     * @See <a href="https://github.com/OpenFeign/feign/issues/1927">https://github.com/OpenFeign/feign/issues/1927</a>
     */
    @Getter
    @Builder
    public static class Request implements OauthTokenRequest {
        private String grant_type;
        private String client_id;
        private String client_secret;
        private String code;
        private String state;
    }

    public record Response(
            String token_type,
            String access_token,
            Integer expires_in,
            String refresh_token,
            String error,
            String error_description) implements OauthTokenResponse {

        @Override
        public String getAccessToken() {
            return access_token;
        }
    }
}
