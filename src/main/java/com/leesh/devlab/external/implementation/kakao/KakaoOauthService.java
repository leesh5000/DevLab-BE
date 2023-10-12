package com.leesh.devlab.external.implementation.kakao;

import com.leesh.devlab.external.OauthService;
import com.leesh.devlab.external.OauthAttributes;
import com.leesh.devlab.external.OauthToken;
import com.leesh.devlab.external.implementation.kakao.client.KakaoApiClient;
import com.leesh.devlab.external.implementation.kakao.client.KakaoAuthClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class KakaoOauthService implements OauthService {

    private KakaoAuthClient kakaoAuthClient;
    private KakaoApiClient kakaoApiClient;
    private String clientId;
    private String clientSecret;
    private String redirectUri;

    public KakaoOauthService(
            KakaoAuthClient kakaoAuthClient,
            KakaoApiClient kakaoApiClient,
            @Value("${oauth.kakao.id}") String clientId,
            @Value("${oauth.kakao.secret}") String clientSecret,
            @Value("${oauth.kakao.redirect-uri}") String redirectUri) {

        this.kakaoAuthClient = kakaoAuthClient;
        this.kakaoApiClient = kakaoApiClient;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;

    }

    @Override
    public OauthToken fetchToken(String authorizationCode) {

        String contentType = "application/x-www-form-urlencoded;charset=utf-8";

        Map<String, Object> request = new HashMap<>();
        request.put("grant_type", "authorization_code");
        request.put("client_id", clientId);
        request.put("client_secret", clientSecret);
        request.put("code", authorizationCode);
        request.put("redirect_uri", redirectUri);

        return kakaoAuthClient.fetchToken(contentType, request);
    }

    @Override
    public OauthAttributes fetchAttributes(String accessToken) {

        String contentType = "application/x-www-form-urlencoded;charset=utf-8";

        return kakaoApiClient.fetchAttributes(contentType, "Bearer " + accessToken);
    }

}
