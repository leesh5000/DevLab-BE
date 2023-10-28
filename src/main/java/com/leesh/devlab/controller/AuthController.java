package com.leesh.devlab.controller;

import com.leesh.devlab.constant.ErrorCode;
import com.leesh.devlab.constant.TokenType;
import com.leesh.devlab.constant.dto.*;
import com.leesh.devlab.exception.custom.BusinessException;
import com.leesh.devlab.service.AuthService;
import com.leesh.devlab.service.CookieService;
import com.leesh.devlab.service.MailService;
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
    private final MailService mailService;

    @PostMapping(path = "/oauth-login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponseDto> oauthLogin(@RequestBody OauthLoginRequestDto requestDto) {

        LoginResponseDto responseBody = authService.oauthLogin(requestDto);

        ResponseCookie cookie = cookieService.generateCookie(
                responseBody.tokenInfo().refreshToken().getTokenType().name(),
                responseBody.tokenInfo().refreshToken().getValue(),
                responseBody.tokenInfo().refreshToken().getExpiresInSeconds());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(responseBody);
    }


    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegisterResponseDto> register(@RequestBody @Valid RegisterRequestDto requestDto) {

        RegisterResponseDto responseDto = authService.register(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto requestDto) {

        LoginResponseDto responseBody = authService.login(requestDto);

        ResponseCookie cookie = cookieService.generateCookie(
                responseBody.tokenInfo().refreshToken().getTokenType().name(),
                responseBody.tokenInfo().refreshToken().getValue(),
                responseBody.tokenInfo().refreshToken().getExpiresInSeconds());

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
    public ResponseEntity<LoginResponseDto> refreshToken(HttpServletRequest request) {

        Cookie cookie = cookieService.extractCookies(request, TokenType.REFRESH.name());

        LoginResponseDto tokenInfoDto = authService.refreshToken(cookie.getValue());

        return ResponseEntity.ok(tokenInfoDto);
    }

    @PostMapping(path = "/find-account", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> findAccount(@RequestBody FindAccount request) {

        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/id-checks")
    public ResponseEntity<Void> checkLoginId(@RequestParam String id) {

        memberService.checkLoginId(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/nickname-checks")
    public ResponseEntity<Void> checkNickname(@RequestParam String nickname) {

        memberService.checkNickname(nickname);

        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/email-verifications")
    public ResponseEntity<Void> verifyEmail(@RequestParam String email) {

        String verificationCode = authService.generateVerificationCode();
        int expiresInSeconds = 5 * 60;

        mailService.sendMail(email,
                "[DevLab] 이메일 인증번호 안내",
                "[이메일 인증번호] " + verificationCode + "\n" +
                "인증번호 유효시간은 " + expiresInSeconds / 60 + "분 입니다.");

        ResponseCookie responseCookie = cookieService.generateCookie(email, verificationCode, expiresInSeconds);

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .build();
    }

    @GetMapping(path = "/email-confirms")
    public ResponseEntity<Void> confirmEmail(HttpServletRequest request, @RequestParam String email, @RequestParam String code) {

        Cookie cookie = cookieService.extractCookies(request, email);

        if (!cookie.getValue().equals(code)) {
            throw new BusinessException(ErrorCode.WRONG_VERIFICATION_CODE, "user input = " + code + " , " + "verification code = " + cookie.getValue());
        }

        return ResponseEntity.noContent().build();
    }
}
