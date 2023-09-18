package com.leesh.devlab.external.implementation.google.client;

import com.leesh.devlab.external.OauthAuthClient;
import com.leesh.devlab.external.OauthTokenRequest;
import com.leesh.devlab.external.implementation.google.dto.GoogleToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(url = "https://oauth2.googleapis.com", name = "googleAuthClient")
public interface GoogleOauthAuthClient extends OauthAuthClient {

    @PostMapping(value = "/token", consumes = "application/json")
    @Override
    GoogleToken.Response requestToken(@RequestHeader("Content-Type") String contentType, OauthTokenRequest request);

}
