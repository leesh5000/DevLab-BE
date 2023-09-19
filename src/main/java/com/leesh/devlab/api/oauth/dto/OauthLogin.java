package com.leesh.devlab.api.oauth.dto;

import com.leesh.devlab.constant.OauthType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OauthLogin {

    public record Request(OauthType oauthType, String authorizationCode) {
    }
}
