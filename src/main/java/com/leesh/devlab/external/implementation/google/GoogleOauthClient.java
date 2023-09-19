package com.leesh.devlab.external.implementation.google;

import com.leesh.devlab.external.OauthClient;
import com.leesh.devlab.external.abstraction.dto.OauthMemberInfo;
import com.leesh.devlab.external.abstraction.dto.OauthTokenResponse;
import com.leesh.devlab.external.implementation.google.client.GoogleOauthApiClient;
import com.leesh.devlab.external.implementation.google.client.GoogleOauthAuthClient;
import com.leesh.devlab.external.implementation.google.dto.GoogleToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class GoogleOauthClient extends OauthClient {

    public GoogleOauthClient(GoogleOauthAuthClient oauthAuthClient, GoogleOauthApiClient oauthApiClient,
                             @Value("${oauth.google.id}") String clientId,
                             @Value("${oauth.google.secret}") String clientSecret,
                             @Value("${oauth.google.redirect-uri}") String redirectUri) {

        super(oauthAuthClient, oauthApiClient);
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;

    }

    @Override
    public OauthTokenResponse requestToken(String authorizationCode) {

        GoogleToken.Request request = GoogleToken.Request.builder()
                .client_id(clientId)
                .client_secret(clientSecret)
                .grant_type("authorization_code")
                .code(authorizationCode)
                .redirect_uri(redirectUri)
                .build();

        return oauthAuthClient.requestToken(MediaType.APPLICATION_FORM_URLENCODED_VALUE, request);
    }

    @Override
    public OauthMemberInfo requestMemberInfo(String accessToken) {
        return oauthApiClient.requestMemberInfo(MediaType.APPLICATION_JSON_VALUE, accessToken);
    }
}
