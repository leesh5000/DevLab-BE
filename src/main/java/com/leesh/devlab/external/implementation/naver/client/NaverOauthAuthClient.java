package com.leesh.devlab.external.implementation.naver.client;

import com.leesh.devlab.external.abstraction.client.OauthAuthClient;
import com.leesh.devlab.external.abstraction.dto.OauthTokenRequest;
import com.leesh.devlab.external.implementation.naver.dto.NaverToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(url = "https://nid.naver.com", name = "naverAuthClient")
public interface NaverOauthAuthClient extends OauthAuthClient {

    @PostMapping(value = "/oauth2.0/token", consumes = "application/json")
    @Override
    NaverToken.Response requestToken(@RequestHeader("Content-Type") String contentType,
                            @SpringQueryMap OauthTokenRequest request
    );

}
