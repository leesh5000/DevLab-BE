package com.leesh.devlab.external;

import com.leesh.devlab.external.abstraction.client.OauthApiClient;
import com.leesh.devlab.external.abstraction.client.OauthAuthClient;
import com.leesh.devlab.external.abstraction.dto.OauthMemberInfo;
import com.leesh.devlab.external.abstraction.dto.OauthTokenResponse;

/**
 * <p>
 *     소셜 로그인 시도한 유저의 Oauth Provider와 통신하는 클라이언트를 추상화한 인터페이스 <br>
 *     구현체는 {@link com.leesh.devlab.external.OauthClientFactory}에서 Bean 이름으로 가져온다.
 * </p>
 */
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
