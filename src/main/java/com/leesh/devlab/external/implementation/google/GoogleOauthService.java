package com.leesh.devlab.external.implementation.google;

import com.leesh.devlab.external.OauthService;
import com.leesh.devlab.external.OauthMemberInfo;
import com.leesh.devlab.external.OauthToken;
import com.leesh.devlab.external.implementation.google.client.GoogleAuthClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class GoogleOauthService implements OauthService {

    private GoogleAuthClient googleAuthClient;
    private String clientId;
    private String clientSecret;
    private String redirectUri;

    public GoogleOauthService(GoogleAuthClient googleAuthClient,
                              @Value("${oauth.google.id}") String clientId,
                              @Value("${oauth.google.secret}") String clientSecret,
                              @Value("${oauth.google.redirect-uri}") String redirectUri) {
        this.googleAuthClient = googleAuthClient;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;

    }

    @Override
    public OauthToken requestToken(String authorizationCode) {

        Map<String, Object> request = new HashMap<>();
        request.put("grant_type", "authorization_code");
        request.put("client_id", clientId);
        request.put("client_secret", clientSecret);
        request.put("code", authorizationCode);
        request.put("redirect_uri", redirectUri);

        return googleAuthClient.requestToken(MediaType.APPLICATION_FORM_URLENCODED_VALUE, request);
    }

    @Override
    public OauthMemberInfo requestMemberInfo(String accessToken) {
        return googleAuthClient.requestMemberInfo(MediaType.APPLICATION_JSON_VALUE, accessToken);
    }
}
