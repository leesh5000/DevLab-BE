 package com.leesh.devlab.external;

 import com.leesh.devlab.configuration.ApplicationContextProvider;
 import com.leesh.devlab.constant.OauthType;
 import com.leesh.devlab.external.implementation.google.GoogleOauthClient;
 import com.leesh.devlab.external.implementation.kakao.KakaoOauthClient;
 import com.leesh.devlab.external.implementation.naver.NaverOauthClient;
 import lombok.RequiredArgsConstructor;
 import org.springframework.context.ApplicationContext;
 import org.springframework.stereotype.Component;

 import java.util.Map;

 /**
  * <p>
  *     {@link OauthClient}의 팩토리 매서드<br>
  *     새로운 Oauth Client 구현체가 추가되면 {@link OauthClientFactory}에 Bean Name을 추가 등록
  * </p>
  */
 @RequiredArgsConstructor
@Component
public class OauthClientFactory {

    private final Map<String, OauthClient> oauthClients;
    private final ApplicationContextProvider contextProvider;

    public OauthClient getService(OauthType type) {

        ApplicationContext context = contextProvider.getContext();

        String oauthClientBeanName = switch (type) {
            case KAKAO -> context.getBeanNamesForType(KakaoOauthClient.class)[0];
            case NAVER -> context.getBeanNamesForType(NaverOauthClient.class)[0];
            case GOOGLE -> context.getBeanNamesForType(GoogleOauthClient.class)[0];
        };

        if (!oauthClients.containsKey(oauthClientBeanName)) {
            throw new IllegalArgumentException("OauthServiceFactory.getService() : " +
                    "oauthClientBeanName is not exist. oauthClientBeanName = " + oauthClientBeanName);
        }

        return oauthClients.get(oauthClientBeanName);
    }
}
