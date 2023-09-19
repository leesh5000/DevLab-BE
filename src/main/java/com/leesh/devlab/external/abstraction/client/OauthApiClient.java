package com.leesh.devlab.external.abstraction.client;

import com.leesh.devlab.external.abstraction.dto.OauthMemberInfo;

public interface OauthApiClient {

    OauthMemberInfo requestMemberInfo(String contentType, String accessToken);

}
