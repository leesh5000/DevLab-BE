package com.leesh.devlab.api.auth;

import com.leesh.devlab.api.auth.dto.OauthLogin;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.leesh.devlab.api.auth.dto.OauthLogin.Request;

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
    public ResponseEntity<OauthLogin.Response> oauthLogin(@RequestBody Request request) {

        OauthLogin.Response response = authService.oauthLogin(request);

        return ResponseEntity.ok(response);
    }

}
