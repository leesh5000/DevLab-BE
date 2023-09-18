package com.leesh.devlab.external;

public interface OauthApiClient {

    OauthMemberInfo requestMemberInfo(String contentType, String accessToken);

}
