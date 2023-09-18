package com.leesh.devlab.api.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.leesh.devlab.api.oauth.dto.OauthLogin.Request;

@RequiredArgsConstructor
@RequestMapping("/api/oauth")
@RestController
public class OauthController {

    private final OauthService oauthService;

    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> oauthLogin(@RequestBody Request request) {

        // AccessToken을 가져온다.
        oauthService.oauthLogin(request);

        return ResponseEntity.ok("");
    }

}
