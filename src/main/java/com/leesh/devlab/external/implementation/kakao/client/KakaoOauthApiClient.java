package com.leesh.devlab.external.implementation.kakao.client;

import com.leesh.devlab.external.abstraction.client.OauthApiClient;
import com.leesh.devlab.external.implementation.kakao.dto.KakaoMemberInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(url = "https://kapi.kakao.com", name = "kakaoApiClient")
public interface KakaoOauthApiClient extends OauthApiClient {

    @GetMapping(value = "/v2/user/me", consumes = "application/json")
    @Override
    KakaoMemberInfo.Response requestMemberInfo(@RequestHeader("Content-type") String contentType,
                                               @RequestHeader("Authorization") String accessToken);

}
