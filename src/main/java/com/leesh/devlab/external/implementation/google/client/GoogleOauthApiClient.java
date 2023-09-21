package com.leesh.devlab.external.implementation.google.client;

import com.leesh.devlab.external.abstraction.client.OauthApiClient;
import com.leesh.devlab.external.implementation.google.dto.GoogleMemberInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(url = "https://www.googleapis.com/oauth2/v3", name = "googleApiClient")
public interface GoogleOauthApiClient extends OauthApiClient {

    @PostMapping(value = "/userinfo?access_token={accessToken}", consumes = "application/json")
    @Override
    GoogleMemberInfo.Response requestMemberInfo(@RequestHeader("Content-type") String contentType,
                                       @PathVariable("accessToken") String accessToken);

}
