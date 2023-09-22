package com.leesh.devlab.external.implementation.kakao.client;

import com.leesh.devlab.external.implementation.kakao.dto.KakaoToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(url = "https://kauth.kakao.com", name = "kakaoAuthClient")
public interface KakaoAuthClient {

    @PostMapping(value = "/oauth/token", consumes = "application/json")
    KakaoToken requestToken(@RequestHeader("Content-Type") String contentType, @SpringQueryMap Map<String, Object> request);

}
