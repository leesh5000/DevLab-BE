package com.leesh.devlab.external;

public abstract class OauthClient {

    protected OauthAuthClient oauthAuthClient;
    protected OauthApiClient oauthApiClient;
    protected String clientId;
    protected String clientSecret;
    protected String redirectUri;

    protected OauthClient(OauthAuthClient oauthAuthClient, OauthApiClient oauthApiClient) {
        this.oauthAuthClient = oauthAuthClient;
        this.oauthApiClient = oauthApiClient;
    }

    public abstract OauthTokenResponse requestToken(String authorizationCode);

    public abstract OauthMemberInfo requestMemberInfo(String accessToken);

}
