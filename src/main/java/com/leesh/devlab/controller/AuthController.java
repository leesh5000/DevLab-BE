package com.leesh.devlab.controller;

import com.leesh.devlab.dto.*;
import com.leesh.devlab.exception.ErrorCode;
import com.leesh.devlab.exception.custom.BusinessException;
import com.leesh.devlab.service.AuthService;
import com.leesh.devlab.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController {

    private final AuthService authService;
    private final MemberService memberService;
    private final String REFRESH_TOKEN = "refresh_token";

    @PostMapping(path = "/oauth-login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Login.Response> oauthLogin(@RequestBody OauthLogin.Request request, HttpServletResponse response) {

        Login.Response body = authService.oauthLogin(request);
        Cookie cookie = generateCookie(body);
        response.addCookie(cookie);

        return ResponseEntity.ok(body);
    }


    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegisterInfo.Response> register(@RequestBody @Valid RegisterInfo.Request request) {

        RegisterInfo.Response responseDto = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Login.Response> login(@RequestBody @Valid Login.Request request, HttpServletResponse response) {

        Login.Response body = authService.login(request);

        Cookie cookie = generateCookie(body);
        response.addCookie(cookie);

        return ResponseEntity.ok(body);
    }

    @PostMapping(path = "/refresh-token", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenRefreshInfo> refreshToken(HttpServletRequest request) {

        Cookie cookie = extractCookies(request);

        TokenRefreshInfo refreshDtoTokenInfo = authService.refreshToken(cookie.getValue());

        return ResponseEntity.ok(refreshDtoTokenInfo);
    }

    private Cookie extractCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            throw new BusinessException(ErrorCode.NOT_EXIST_REFRESH_TOKEN, "refresh token is empty.");
        }

        return Arrays.stream(cookies)
                .filter(c -> c.getName().equals(REFRESH_TOKEN))
                .findAny()
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_REFRESH_TOKEN, "refresh token is empty."));
    }

    @PostMapping(path = "/find-account", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> findAccount(@RequestBody FindAccount request) {

        authService.findAccount(request);

        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/id-checks")
    public ResponseEntity<Void> checkId(@RequestParam String id) {

        memberService.validateLoginId(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/nickname-checks")
    public ResponseEntity<Void> checkNickname(@RequestParam String nickname) {

        memberService.validateNickname(nickname);

        return ResponseEntity.noContent().build();
    }


    private Cookie generateCookie(Login.Response body) {
        Cookie cookie = new Cookie(REFRESH_TOKEN, body.refreshToken().getValue());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(body.refreshToken().getExpiresIn());
        return cookie;
    }
}
