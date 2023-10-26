package com.leesh.devlab.service;

import com.leesh.devlab.constant.ErrorCode;
import com.leesh.devlab.exception.custom.BusinessException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@RequiredArgsConstructor
@Service
public class CookieService {

    public static final String COOKIE_DOMAIN = "devlab.com";


    public ResponseCookie generateCookie(String key, String value, int maxAgeSeconds) {

        return ResponseCookie.from(encode(key), value)
                .httpOnly(true)
                .domain(COOKIE_DOMAIN)
                .sameSite("None")
                .secure(true)
                .path("/")
                .maxAge(maxAgeSeconds)
                .build();
    }

    public Cookie extractCookies(HttpServletRequest request, String key) {

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            throw new BusinessException(ErrorCode.NOT_EXIST_COOKIE, "cookie is empty.");
        }

        return Arrays.stream(cookies)
                .filter(c -> c.getName().equals(encode(key)))
                .findAny()
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN_NAME, "token name is not valid."));
    }

    public static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

}
