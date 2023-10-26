package com.leesh.devlab.external.implementation.naver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leesh.devlab.constant.OauthType;
import com.leesh.devlab.external.OauthAttributes;
import lombok.Getter;

@Getter
public class NaverAttributes implements OauthAttributes {

    private @JsonProperty("resultcode") String resultCode;
    private String message;
    private Response response;

    private NaverAttributes() {
    }

    @Getter
    private static class Response {

        private Response() {
        }

        private String id;

    }

    @Override
    public String getId() {
        return getOauthType() + "@" + response.id;
    }

    @Override
    public OauthType getOauthType() {
        return OauthType.NAVER;
    }

}
