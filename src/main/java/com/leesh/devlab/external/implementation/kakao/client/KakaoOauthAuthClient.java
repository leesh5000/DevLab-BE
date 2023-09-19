package com.leesh.devlab.external.implementation.kakao.client;

import com.leesh.devlab.external.abstraction.client.OauthAuthClient;
import com.leesh.devlab.external.abstraction.dto.OauthTokenRequest;
import com.leesh.devlab.external.implementation.kakao.dto.KakaoToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(url = "https://kauth.kakao.com", name = "kakaoAuthClient")
public interface KakaoOauthAuthClient extends OauthAuthClient {

    @PostMapping(value = "/oauth/token", consumes = "application/json")
    @Override
    KakaoToken.Response requestToken(@RequestHeader("Content-Type") String contentType, @SpringQueryMap OauthTokenRequest request);

}
