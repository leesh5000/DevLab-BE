package com.leesh.devlab.external;

/**
 * <p>
 *     소셜 로그인 시도한 유저의 Oauth Provider와 통신하는 클라이언트를 추상화한 인터페이스 <br>
 *     구현체는 {@link OauthServiceFactory}에서 Bean 이름으로 가져온다.
 * </p>
 */
public interface OauthService {

    OauthToken requestToken(String authorizationCode);

    OauthAttributes requestMemberInfo(String accessToken);

}
