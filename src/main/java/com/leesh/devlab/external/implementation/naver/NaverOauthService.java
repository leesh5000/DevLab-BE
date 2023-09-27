package com.leesh.devlab.external.implementation.naver;

import com.leesh.devlab.external.OauthService;
import com.leesh.devlab.external.OauthMemberInfo;
import com.leesh.devlab.external.OauthToken;
import com.leesh.devlab.external.implementation.naver.client.NaverApiClient;
import com.leesh.devlab.external.implementation.naver.client.NaverAuthClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class NaverOauthService implements OauthService {

    private NaverAuthClient authClient;
    private NaverApiClient apiClient;
    private String clientId;
    private String clientSecret;


    public NaverOauthService(
            NaverAuthClient authClient,
            NaverApiClient apiClient,
            @Value("${oauth.naver.id}") String clientId,
            @Value("${oauth.naver.secret}") String clientSecret) {

        this.authClient = authClient;
        this.apiClient = apiClient;
        this.clientId = clientId;
        this.clientSecret = clientSecret;

    }

    @Override
    public OauthToken requestToken(String authorizationCode) {

        Map<String, Object> request = new HashMap<>();
        request.put("grant_type", "authorization_code");
        request.put("client_id", clientId);
        request.put("client_secret", clientSecret);
        request.put("code", authorizationCode);
        request.put("state", "RANDOM_STATE");

        return authClient.requestToken(MediaType.APPLICATION_FORM_URLENCODED_VALUE, request);
    }

    @Override
    public OauthMemberInfo requestMemberInfo(String accessToken) {
        return apiClient.requestMemberInfo(MediaType.APPLICATION_JSON_VALUE, "Bearer " + accessToken);
    }
}
