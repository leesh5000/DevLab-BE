package com.leesh.devlab.api.oauth;

import com.leesh.devlab.api.oauth.dto.*;
import com.leesh.devlab.jwt.dto.MemberInfo;
import com.leesh.devlab.resolver.LoginMember;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<OauthLoginDto.Response> oauthLogin(@RequestBody OauthLoginDto.Request request) {

        OauthLoginDto.Response response = authService.oauthLogin(request);

        return ResponseEntity.ok(response);
    }

    /**
     * 액세스 토큰 갱신 API
     */
    @GetMapping(path = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RefreshTokenDto> refreshToken(HttpServletRequest request) {

        String refreshToken = extractAuthorization(request);

        RefreshTokenDto refreshTokenDto = authService.refreshToken(refreshToken);

        return ResponseEntity.ok(refreshTokenDto);
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
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterDto.Request request) {

        authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 일반 계정 로그인 API
     */
    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginDto.Response> login(@RequestBody @Valid LoginDto.Request request) {

        LoginDto.Response response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    /**
     * 아이디/비밀번호 찾기 API
     */
    @PostMapping(path = "/find", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findIdAndPassword(@RequestBody @Valid FindDto.Request request) {

        authService.findIdAndPassword(request);

        return ResponseEntity.noContent().build();
    }

}
