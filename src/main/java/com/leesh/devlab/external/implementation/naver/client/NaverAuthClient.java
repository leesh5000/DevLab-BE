package com.leesh.devlab.external.implementation.naver.client;

import com.leesh.devlab.external.implementation.naver.dto.NaverToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(url = "https://nid.naver.com", name = "naverAuthClient")
public interface NaverAuthClient {

    @PostMapping(value = "/oauth2.0/token", consumes = "application/json")
    NaverToken requestToken(@RequestHeader("Content-Type") String contentType,
                            @SpringQueryMap Map<String, Object> request
    );

}
