package com.leesh.devlab.configuration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Encoding;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

@DependsOn("localeConfiguration")
@Configuration
public class MessageConfiguration implements WebMvcConfigurer {

    /**
     * <code>Locale.getDefault</code>의 기본 Locale 값은 아래 파일에서 설정
     * {@link LocaleConfiguration#LocaleConfiguration(String)}  LocaleConfiguration}
     * @return
     */
    @Bean
    public LocaleResolver localeResolver() {

        // AcceptHeaderLocaleResolver : HTTP Request의 Accept-Header 필드를 보고 locale 결정
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.getDefault());

        return localeResolver;
    }

    @Bean
    public MessageSource messageSource() {

        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();

        messageSource.setBasename("classpath:/messages/messages");
        messageSource.setDefaultEncoding(Encoding.DEFAULT_CHARSET.toString());
        messageSource.setDefaultLocale(Locale.getDefault());
        messageSource.setCacheSeconds(600);

        return messageSource;
    }

    @Bean
    public MessageSourceAccessor messageSourceAccessor (@Autowired MessageSource messageSource) {

        return new MessageSourceAccessor(messageSource);
    }

}
