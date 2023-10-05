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
    public ResponseEntity<Login.Response> oauthLogin(@RequestBody OauthLogin.Request request) {

        Login.Response response = authService.oauthLogin(request);

        return ResponseEntity.ok(response);
    }


    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegisterInfo.Response> register(@RequestBody @Valid RegisterInfo.Request request) {

        RegisterInfo.Response responseDto = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Login.Response> login(@RequestBody @Valid Login.Request request) {

        Login.Response response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/refresh-token", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenRefreshInfo> refreshToken(HttpServletRequest request) {

        String refreshToken = extractAuthorization(request);

        TokenRefreshInfo refreshDtoTokenInfo = authService.refreshToken(refreshToken);

        return ResponseEntity.ok(refreshDtoTokenInfo);
    }

    @PostMapping(path = "/find-account", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> findAccount(@RequestBody FindAccount request) {

        authService.findAccount(request);

        return ResponseEntity.noContent().build();
    }

}
