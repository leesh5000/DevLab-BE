package com.leesh.devlab.global.configuration;

import com.leesh.devlab.domain.member.constant.OauthType;
import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 소셜 로그인 시도한 유저 요청의 경우에 알맞은 Oauth Provider를 찾기 위해 BeanName을 찾기 위해 사용
 * Ref {@link com.leesh.devlab.external.OauthClientFactory#getService(OauthType)}
 */
@Getter
@Component
public class ApplicationContextProvider implements ApplicationContextAware {

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

}
