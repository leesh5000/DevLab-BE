 package com.leesh.devlab.external;

import com.leesh.devlab.domain.member.constant.OauthType;
import com.leesh.devlab.external.implementation.google.GoogleOauthClient;
import com.leesh.devlab.external.implementation.kakao.KakaoOauthClient;
import com.leesh.devlab.external.implementation.naver.NaverOauthClient;
import com.leesh.devlab.global.configuration.ApplicationContextProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

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
