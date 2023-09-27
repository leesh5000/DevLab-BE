package com.leesh.devlab.api.auth;

import com.leesh.devlab.api.auth.dto.LoginInfo;
import com.leesh.devlab.api.auth.dto.OauthLoginInfo;
import com.leesh.devlab.api.auth.dto.RegisterInfo;
import com.leesh.devlab.api.auth.dto.TokenRefreshDto;
import com.leesh.devlab.jwt.dto.MemberInfo;
import com.leesh.devlab.resolver.LoginMember;
import com.leesh.devlab.service.AuthService;
import com.leesh.devlab.validator.Email;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.leesh.devlab.util.HttpHeaderUtils.extractAuthorization;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    /**
     * 소셜 계정 로그인 API
     * @param request
     * @return
     */
    @PostMapping(path = "/oauth-login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OauthLoginInfo.Response> oauthLogin(@RequestBody OauthLoginInfo.Request request) {

        OauthLoginInfo.Response response = authService.oauthLogin(request);

        return ResponseEntity.ok(response);
    }

    /**
     * 액세스 토큰 갱신 API
     */
    @GetMapping(path = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenRefreshDto> refresh(HttpServletRequest request) {

        String refreshToken = extractAuthorization(request);

        TokenRefreshDto tokenRefreshDtoDto = authService.refresh(refreshToken);

        return ResponseEntity.ok(tokenRefreshDtoDto);
    }

    @GetMapping(path = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> logout(@LoginMember MemberInfo memberInfo) {

        authService.logout(memberInfo);

        return ResponseEntity.noContent().build();
    }

    /**
     * 일반 계정 회원가입 API
     */
    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterInfo.Request request) {

        authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 일반 계정 로그인 API
     */
    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginInfo.Response> login(@RequestBody @Valid LoginInfo.Request request) {

        LoginInfo.Response response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    /**
     * 아이디/비밀번호 찾기 API
     */
    @GetMapping(path = "/find")
    public ResponseEntity<Void> findIdAndPassword(@RequestParam @Email String email) {

        authService.findIdAndPassword(email);

        return ResponseEntity.noContent().build();
    }

    /**
     * 이메일 인증 API
     */
    @GetMapping(path = "/email-verify")
    public ResponseEntity<Void> emailVerify(@RequestParam @Email String email, HttpServletRequest httpRequest) {

        HttpSession session = httpRequest.getSession();

        authService.emailVerify(email, session);

        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/email-confirm")
    public ResponseEntity<Void> emailConfirm(@RequestParam @Email String email, @RequestParam String code, @LoginMember MemberInfo memberInfo, HttpServletRequest httpRequest) {

        HttpSession session = httpRequest.getSession();

        authService.emailConfirm(email, code, memberInfo, session);

        return ResponseEntity.noContent().build();
    }

}
