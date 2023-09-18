package com.leesh.devlab.external.implementation.naver;

import com.leesh.devlab.external.OauthClient;
import com.leesh.devlab.external.OauthMemberInfo;
import com.leesh.devlab.external.OauthTokenRequest;
import com.leesh.devlab.external.OauthTokenResponse;
import com.leesh.devlab.external.implementation.naver.client.NaverOauthApiClient;
import com.leesh.devlab.external.implementation.naver.client.NaverOauthAuthClient;
import com.leesh.devlab.external.implementation.naver.dto.NaverToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class NaverOauthClient extends OauthClient {

    public NaverOauthClient(
            NaverOauthAuthClient authClient,
            NaverOauthApiClient apiClient,
            @Value("${oauth.naver.id}") String clientId,
            @Value("${oauth.naver.secret}") String clientSecret,
            @Value("${oauth.naver.redirect-uri}") String redirectUri) {

        super(authClient, apiClient);
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;

    }

    @Override
    public OauthTokenResponse requestToken(String authorizationCode) {

        OauthTokenRequest request = NaverToken.Request.builder()
                .grant_type("authorization_code")
                .client_id(clientId)
                .client_secret(clientSecret)
                .code(authorizationCode)
                .state("RANDOM_STATE")
                .build();

        return oauthAuthClient.requestToken(MediaType.APPLICATION_FORM_URLENCODED_VALUE, request);
    }

    @Override
    public OauthMemberInfo requestMemberInfo(String accessToken) {
        return oauthApiClient.requestMemberInfo(MediaType.APPLICATION_JSON_VALUE, "Bearer " + accessToken);
    }
}
