package com.leesh.devlab.config;

import com.leesh.devlab.interceptor.AuthInterceptor;
import com.leesh.devlab.interceptor.AuthInterceptorProxy;
import com.leesh.devlab.resolver.LoginMemberArgResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static com.leesh.devlab.interceptor.AuthInterceptorProxy.RequestMethod;

@RequiredArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final LoginMemberArgResolver loginMemberArgResolver;

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/api/**")
                // client에서 withCredentials: true로 요청을 보내면 Access-Control-Allow-Origin은 *를 허용하지 않는다. (정책)
                .allowedOrigins("http://localhost:5173/")
                .allowCredentials(true)
                .allowedMethods(
                        HttpMethod.GET.name(),
                        HttpMethod.POST.name(),
                        HttpMethod.PUT.name(),
                        HttpMethod.PATCH.name(),
                        HttpMethod.DELETE.name(),
                        HttpMethod.OPTIONS.name())
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
                .excludePathPatterns("/api/members/ids/**", RequestMethod.GET)
                .addPathPatterns("/api/members/me", RequestMethod.GET)
        ;

        return proxy;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginMemberArgResolver);
    }

}
