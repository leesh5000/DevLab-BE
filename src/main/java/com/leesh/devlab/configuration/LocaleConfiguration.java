package com.leesh.devlab.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.Locale;

/**
 * 서버의 기본 Locale 설정을 위한 클래스 파일
 */
@Configuration
public class LocaleConfiguration {

    /**
     * 프로퍼티에서 기본 로케일을 받아오도록 설정
     * @See application.yml
     */
    public LocaleConfiguration(@Value("${custom.locale}") String locale) {
        Locale defaultLocale = StringUtils.parseLocale(locale);
        assert defaultLocale != null;
        Locale.setDefault(defaultLocale);
    }

}
