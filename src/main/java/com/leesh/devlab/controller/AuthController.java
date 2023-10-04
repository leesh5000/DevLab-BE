package com.leesh.devlab.controller;

import com.leesh.devlab.dto.*;
import com.leesh.devlab.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.leesh.devlab.util.HttpHeaderUtil.extractAuthorization;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    @PostMapping(path = "/oauth-login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OauthLogin.Response> oauthLogin(@RequestBody OauthLogin.Request request) {

        OauthLogin.Response response = authService.oauthLogin(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/refresh-token", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RefreshTokenInfo> refreshToken(HttpServletRequest request) {

        String refreshToken = extractAuthorization(request);

        RefreshTokenInfo refreshDtoTokenInfo = authService.refreshToken(refreshToken);

        return ResponseEntity.ok(refreshDtoTokenInfo);
    }

    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegisterInfo.Response> register(@RequestBody @Valid RegisterInfo.Request requestDto) {

        RegisterInfo.Response responseDto = authService.register(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Login.Response> login(@RequestBody @Valid Login.Request request) {

        Login.Response response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/find-account")
    public ResponseEntity<Void> findAccount(@RequestBody FindAccount requestDto) {

        authService.findAccount(requestDto);

        return ResponseEntity.noContent().build();
    }

}
