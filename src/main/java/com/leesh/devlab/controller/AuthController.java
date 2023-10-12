package com.leesh.devlab.controller;

import com.leesh.devlab.dto.*;
import com.leesh.devlab.jwt.TokenType;
import com.leesh.devlab.service.AuthService;
import com.leesh.devlab.service.CookieService;
import com.leesh.devlab.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController {

    private final AuthService authService;
    private final MemberService memberService;
    private final CookieService cookieService;

    @PostMapping(path = "/oauth-login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Login.Response> oauthLogin(@RequestBody OauthLogin.Request request) {

        Login.Response responseBody = authService.oauthLogin(request);

        ResponseCookie cookie = cookieService.generateCookie(
                responseBody.refreshToken().getTokenType().name(),
                responseBody.refreshToken().getValue(),
                responseBody.refreshToken().getExpiresIn());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(responseBody);
    }


    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegisterInfo.Response> register(@RequestBody @Valid RegisterInfo.Request request) {

        RegisterInfo.Response responseDto = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Login.Response> login(@RequestBody @Valid Login.Request request) {

        Login.Response responseBody = authService.login(request);

        ResponseCookie cookie = cookieService.generateCookie(
                responseBody.refreshToken().getTokenType().name(),
                responseBody.refreshToken().getValue(),
                responseBody.refreshToken().getExpiresIn());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(responseBody);
    }

    @DeleteMapping(path = "/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {

        Cookie cookie = cookieService.extractCookies(request, TokenType.REFRESH.name());

        authService.logout(cookie.getValue());
        ResponseCookie responseCookie = cookieService.generateCookie(TokenType.REFRESH.name(), "", 0);

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .build();
    }

    @PostMapping(path = "/refresh-token", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenRefreshInfo> refreshToken(HttpServletRequest request) {

        Cookie cookie = cookieService.extractCookies(request, TokenType.REFRESH.name());

        TokenRefreshInfo refreshDtoTokenInfo = authService.refreshToken(cookie.getValue());

        return ResponseEntity.ok(refreshDtoTokenInfo);
    }

    @PostMapping(path = "/find-account", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> findAccount(@RequestBody FindAccount request) {

        authService.findAccount(request);

        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/validation/id")
    public ResponseEntity<Void> validateLoginId(@RequestParam String id) {

        memberService.validateLoginId(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/validation/nickname")
    public ResponseEntity<Void> validateNickname(@RequestParam String nickname) {

        memberService.validateNickname(nickname);

        return ResponseEntity.noContent().build();
    }

}
