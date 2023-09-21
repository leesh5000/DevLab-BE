package com.leesh.devlab.external.abstraction.client;

import com.leesh.devlab.external.abstraction.dto.OauthTokenRequest;
import com.leesh.devlab.external.abstraction.dto.OauthTokenResponse;

public interface OauthAuthClient {

    OauthTokenResponse requestToken(String contentType, OauthTokenRequest request);

}
