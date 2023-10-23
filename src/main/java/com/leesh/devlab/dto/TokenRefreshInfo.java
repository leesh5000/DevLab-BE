package com.leesh.devlab.dto;

import com.leesh.devlab.jwt.Token;

public record TokenRefreshInfo(String grantType, Token accessToken, UserInfo userInfo) {

    public record UserInfo(Long id, String loginId, String nickname) {

    }

    public static TokenRefreshInfo of(String grantType, Token accessToken, Long id, String loginId, String nickname) {
        return new TokenRefreshInfo(grantType, accessToken, new UserInfo(id, loginId, nickname));
    }

}
