package com.leesh.devlab.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayList;
import java.util.List;

public class AuthInterceptorProxy implements HandlerInterceptor {

    private final AuthInterceptor authInterceptor;
    private final List<PathPattern> includePathPatterns;
    private final List<PathPattern> excludePathPatterns;
    private final PathMatcher pathMatcher;

    private record PathPattern(String pathPattern, RequestMethod requestMethod) {

        public static PathPattern of(String pathPattern, RequestMethod httpMethod) {
            return new PathPattern(pathPattern, httpMethod);
        }

    }

    public enum RequestMethod {
        ANY, GET, POST, PUT, PATCH, DELETE, OPTIONS
    }

    public AuthInterceptorProxy(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
        this.pathMatcher = new AntPathMatcher();
        this.includePathPatterns = new ArrayList<>();
        this.excludePathPatterns = new ArrayList<>();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        boolean isInclude = includePathPatterns.stream()
                .anyMatch(pathPattern -> isMatchPath(request, pathPattern) && isMatchMethod(request, pathPattern));

        boolean isExclude = excludePathPatterns.stream()
                .anyMatch(pathPattern -> isMatchPath(request, pathPattern) && isMatchMethod(request, pathPattern));

        if (isExclude && !isInclude) {
            return true;
        }

        return authInterceptor.preHandle(request, response, handler);
    }

    private static boolean isMatchMethod(HttpServletRequest request, PathPattern pathPattern) {
        return pathPattern.requestMethod() == RequestMethod.ANY || pathPattern.requestMethod().name().equals(request.getMethod());
    }

    private boolean isMatchPath(HttpServletRequest request, PathPattern pathPattern) {
        String requestURI = request.getRequestURI().substring(request.getContextPath().length());
        return pathMatcher.match(pathPattern.pathPattern(), requestURI);
    }

    public AuthInterceptorProxy addPathPatterns(String pathPattern, RequestMethod requestMethod) {
        this.includePathPatterns.add(PathPattern.of(pathPattern, requestMethod));
        return this;
    }

    public AuthInterceptorProxy excludePathPatterns(String pathPattern, RequestMethod requestMethod) {
        this.excludePathPatterns.add(PathPattern.of(pathPattern, requestMethod));
        return this;
    }

}
