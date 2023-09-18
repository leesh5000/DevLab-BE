package com.leesh.devlab.external.implementation.kakao;

import com.leesh.devlab.external.OauthClient;
import com.leesh.devlab.external.OauthMemberInfo;
import com.leesh.devlab.external.OauthTokenRequest;
import com.leesh.devlab.external.OauthTokenResponse;
import com.leesh.devlab.external.implementation.kakao.client.KakaoOauthApiClient;
import com.leesh.devlab.external.implementation.kakao.client.KakaoOauthAuthClient;
import com.leesh.devlab.external.implementation.kakao.dto.KakaoToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KakaoOauthClient extends OauthClient {

    public KakaoOauthClient(
            KakaoOauthAuthClient authClient,
            KakaoOauthApiClient apiClient,
            @Value("${oauth.kakao.id}") String clientId,
            @Value("${oauth.kakao.secret}") String clientSecret,
            @Value("${oauth.kakao.redirect-uri}") String redirectUri) {

        super(authClient, apiClient);
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
    }

    @Override
    public OauthTokenResponse requestToken(String authorizationCode) {

        String contentType = "application/x-www-form-urlencoded;charset=utf-8";

        OauthTokenRequest request = KakaoToken.Request.builder()
                .client_id(clientId)
                .client_secret(clientSecret)
                .grant_type("authorization_code")
                .code(authorizationCode)
                .redirect_uri(redirectUri)
                .build();

        return oauthAuthClient.requestToken(contentType, request);
    }

    @Override
    public OauthMemberInfo requestMemberInfo(String accessToken) {

        String contentType = "application/x-www-form-urlencoded;charset=utf-8";

        return oauthApiClient.requestMemberInfo(contentType, "Bearer " + accessToken);
    }

}
