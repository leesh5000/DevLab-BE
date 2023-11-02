package com.leesh.devlab.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static com.leesh.devlab.config.AuthInterceptorProxy.RequestMethod;

@RequiredArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final LoginMemberArgResolver loginMemberArgResolver;
    @Value("${custom.cors.allowed-origins}")
    private String[] allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowCredentials(true)
                .allowedMethods("*")
                .allowedHeaders("*")
                .maxAge(3600);

    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(authInterceptorProxy())
                .order(1)
                .addPathPatterns("/api/**");
    }

    @Bean
    public AuthInterceptorProxy authInterceptorProxy() {

        AuthInterceptorProxy proxy = new AuthInterceptorProxy(authInterceptor);

        proxy
                .excludePathPatterns("/**", RequestMethod.OPTIONS) // Preflight 요청은 인증 필터를 타지 않도록 설정
                .excludePathPatterns("/api/**", RequestMethod.GET) // GET 요청은 인증 필터를 타지 않도록 설정
                .excludePathPatterns("/api/auth/**", RequestMethod.ANY) // 인증 API는 인증 필터를 타지 않도록 설정
                .excludePathPatterns("/api/health", RequestMethod.GET) // health check API는 인증 필터를 타지 않도록 설정
                .addPathPatterns("/api/members/me", RequestMethod.GET)
                .excludePathPatterns("/docs/**", RequestMethod.GET)
        ;

        return proxy;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginMemberArgResolver);
    }

}
