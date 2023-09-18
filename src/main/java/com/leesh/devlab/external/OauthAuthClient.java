package com.leesh.devlab.external;

public interface OauthAuthClient {

    OauthTokenResponse requestToken(String contentType, OauthTokenRequest request);

}
