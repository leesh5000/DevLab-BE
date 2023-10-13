package com.leesh.devlab.external.implementation.google.client;

import com.leesh.devlab.external.implementation.google.dto.GoogleAttributes;
import com.leesh.devlab.external.implementation.google.dto.GoogleToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(url = "https://oauth2.googleapis.com", name = "googleAuthClient")
public interface GoogleAuthClient {

    @PostMapping(value = "/token", consumes = "application/json")
    GoogleToken fetchToken(@RequestHeader("Content-Type") String contentType, Map<String, Object> request);

    @PostMapping(value = "/tokeninfo?id_token={idToken}", consumes = "application/json")
    GoogleAttributes fetchAttributes(@RequestHeader("Content-type") String contentType,
                                     @PathVariable("idToken") String idToken);

}
