package com.leesh.devlab.dto;

import com.leesh.devlab.jwt.Token;

public record TokenRefreshInfo(String grantType, Token accessToken, UserInfo userInfo) {

    public record UserInfo(String loginId, String nickname) {

    }

    public static TokenRefreshInfo of(String grantType, Token accessToken, String loginId, String nickname) {
        return new TokenRefreshInfo(grantType, accessToken, new UserInfo(loginId, nickname));
    }

}
