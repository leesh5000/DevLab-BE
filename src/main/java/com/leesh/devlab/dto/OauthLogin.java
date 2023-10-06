package com.leesh.devlab.dto;

import com.leesh.devlab.domain.member.OauthType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OauthLogin {

    public record Request(OauthType oauthType, String authorizationCode) {
    }

}
