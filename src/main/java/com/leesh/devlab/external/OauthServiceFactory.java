 package com.leesh.devlab.external;

 import com.leesh.devlab.config.AppContextProvider;
 import com.leesh.devlab.constant.OauthType;
 import com.leesh.devlab.external.implementation.google.GoogleOauthService;
 import com.leesh.devlab.external.implementation.kakao.KakaoOauthService;
 import com.leesh.devlab.external.implementation.naver.NaverOauthService;
 import lombok.RequiredArgsConstructor;
 import org.springframework.context.ApplicationContext;
 import org.springframework.stereotype.Component;

 import java.util.Map;

 /**
  * <p>
  *     {@link OauthService}의 팩토리 매서드<br>
  *     새로운 Oauth Client 구현체가 추가되면 {@link OauthServiceFactory}에 Bean Name을 추가 등록
  * </p>
  */
 @RequiredArgsConstructor
@Component
public class OauthServiceFactory {

    private final Map<String, OauthService> oauthServices;
    private final AppContextProvider contextProvider;

    public OauthService getService(OauthType type) {

        ApplicationContext context = contextProvider.getContext();

        String oauthClientBeanName = switch (type) {
            case KAKAO -> context.getBeanNamesForType(KakaoOauthService.class)[0];
            case NAVER -> context.getBeanNamesForType(NaverOauthService.class)[0];
            case GOOGLE -> context.getBeanNamesForType(GoogleOauthService.class)[0];
        };

        if (!oauthServices.containsKey(oauthClientBeanName)) {
            throw new IllegalArgumentException("OauthServiceFactory.getService() : " +
                    "oauthClientBeanName is not exist. oauthClientBeanName = " + oauthClientBeanName);
        }

        return oauthServices.get(oauthClientBeanName);
    }
}
