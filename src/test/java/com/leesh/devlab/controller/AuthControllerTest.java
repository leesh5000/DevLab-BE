package com.leesh.devlab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leesh.devlab.domain.member.OauthType;
import com.leesh.devlab.dto.*;
import com.leesh.devlab.jwt.GrantType;
import com.leesh.devlab.jwt.Token;
import com.leesh.devlab.jwt.TokenType;
import com.leesh.devlab.jwt.implementation.Jwt;
import com.leesh.devlab.service.AuthService;
import config.WebMvcTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(WebMvcTestConfig.class)
@AutoConfigureRestDocs
class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @MockBean
    private AuthService authService;

    @Test
    void oauthLogin_test() throws Exception {

        // given
        String authorizationCode = "y7iyuzOxjD3AnPOtNDkxlKhVEtdjIBduM7uJboWnDskFxrD9GvitLQpqpnA7fAc4pMvowAo9dJcAAAGGCllssw";
        long now = System.currentTimeMillis();
        Token accessToken = new Jwt(TokenType.ACCESS, "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBQ0NFU1MiLCJpYXQiOjE2NzUyMTA4NzksImV4cCI6MTY3NTIxMTc3OSwidXNlcklkIjoxLCJyb2xlIjoiVVNFUiJ9.X1AfxGWGUPhC5ovt3hcLv8_6Zb8H0Z4yn8tDxHohrTx_kcgTDWIHPt8yDuTHYo9KmqqqIwTQ7VEtMaVyJdqKrQ", TokenType.ACCESS.getExpiresInMills() + now);
        Token refreshToken = new Jwt(TokenType.REFRESH, "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBQ0NFU1MiLCJpYXQiOjE2NzUyMTA4NzksImV4cCI6MTY3NTIxMTc3OSwidXNlcklkIjoxLCJyb2xlIjoiVVNFUiJ9.X1AfxGWGUPhC5ovt3hcLv8_6Zb8H0Z4yn8tDxHohrTx_kcgTDWIHPt8yDuTHYo9KmqqqIwTQ7VEtMaVyJdqKrQ", TokenType.REFRESH.getExpiresInMills() + now);

        OauthLogin.Request request = new OauthLogin.Request(OauthType.NAVER, authorizationCode);
        Login.Response response = new Login.Response(GrantType.BEARER.getType(), accessToken, refreshToken);

        given(authService.oauthLogin(request))
                .willReturn(response);

        // when
        ResultActions result = mvc.perform(post("/api/auth/oauth-login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(request)));

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grant_type").value(GrantType.BEARER.getType()))
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.access_token.token_type").value(TokenType.ACCESS.name()))
                .andExpect(jsonPath("$.access_token.value").value(accessToken.getValue()))
                .andExpect(jsonPath("$.access_token.expired_at").value(accessToken.getExpiredAt()))
                .andExpect(jsonPath("$.refresh_token").exists())
                .andExpect(jsonPath("$.refresh_token.token_type").value(TokenType.REFRESH.name()))
                .andExpect(jsonPath("$.refresh_token.value").value(refreshToken.getValue()))
                .andExpect(jsonPath("$.refresh_token.expired_at").value(refreshToken.getExpiredAt()))
                .andDo(print());

        then(authService).should().oauthLogin(request);

        // API Docs
        result
                .andDo(document("oauth-login",
                        requestFields(
                                fieldWithPath("oauth_type").description("소셜 계정 유형"),
                                fieldWithPath("authorization_code").description("인가 코드")
                        ),
                        responseFields(
                                fieldWithPath("grant_type").description("토큰 인증 유형"),
                                fieldWithPath("access_token").description("액세스 토큰"),
                                fieldWithPath("access_token.token_type").description("토큰 유형"),
                                fieldWithPath("access_token.value").description("토큰 값"),
                                fieldWithPath("access_token.expired_at").description("토큰 만료일"),
                                fieldWithPath("refresh_token").description("리프레쉬 토큰"),
                                fieldWithPath("refresh_token.token_type").description("토큰 유형"),
                                fieldWithPath("refresh_token.value").description("토큰 값"),
                                fieldWithPath("refresh_token.expired_at").description("토큰 만료일")
                        )));

    }

    @Test
    void register_test() throws Exception {

        // given
        RegisterInfo.Request request = new RegisterInfo.Request("test", "test", "test");

        given(authService.register(request))
                .willReturn(new RegisterInfo.Response(1L));

        // when
        var result = mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(request)));

        // then
        result
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.member_id").exists())
                .andDo(print());

        then(authService).should().register(request);

        // API Docs
        result
                .andDo(document("register",
                        requestFields(
                                fieldWithPath("login_id").description("로그인 아이디"),
                                fieldWithPath("password").description("비밀번호"),
                                fieldWithPath("nickname").description("닉네임")
                        ),
                        responseFields(
                                fieldWithPath("member_id").description("생성된 회원 아이디(PK)")
                        )));
    }

    @Test
    void login_test() throws Exception {

        // given
        Login.Request request = new Login.Request("test", "test");

        long now = System.currentTimeMillis();
        Token accessToken = new Jwt(TokenType.ACCESS, "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBQ0NFU1MiLCJpYXQiOjE2NzUyMTA4NzksImV4cCI6MTY3NTIxMTc3OSwidXNlcklkIjoxLCJyb2xlIjoiVVNFUiJ9.X1AfxGWGUPhC5ovt3hcLv8_6Zb8H0Z4yn8tDxHohrTx_kcgTDWIHPt8yDuTHYo9KmqqqIwTQ7VEtMaVyJdqKrQ", TokenType.ACCESS.getExpiresInMills() + now);
        Token refreshToken = new Jwt(TokenType.REFRESH, "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBQ0NFU1MiLCJpYXQiOjE2NzUyMTA4NzksImV4cCI6MTY3NTIxMTc3OSwidXNlcklkIjoxLCJyb2xlIjoiVVNFUiJ9.X1AfxGWGUPhC5ovt3hcLv8_6Zb8H0Z4yn8tDxHohrTx_kcgTDWIHPt8yDuTHYo9KmqqqIwTQ7VEtMaVyJdqKrQ", TokenType.REFRESH.getExpiresInMills() + now);
        Login.Response response = new Login.Response(GrantType.BEARER.getType(), accessToken, refreshToken);

        given(authService.login(request))
                .willReturn(response);

        // when
        var result = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(request)));

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grant_type").value(GrantType.BEARER.getType()))
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.access_token.token_type").value(TokenType.ACCESS.name()))
                .andExpect(jsonPath("$.access_token.value").value(accessToken.getValue()))
                .andExpect(jsonPath("$.access_token.expired_at").value(accessToken.getExpiredAt()))
                .andExpect(jsonPath("$.refresh_token").exists())
                .andExpect(jsonPath("$.refresh_token.token_type").value(TokenType.REFRESH.name()))
                .andExpect(jsonPath("$.refresh_token.value").value(refreshToken.getValue()))
                .andExpect(jsonPath("$.refresh_token.expired_at").value(refreshToken.getExpiredAt()))
                .andDo(print());

        then(authService).should().login(request);

        // API Docs
        result
                .andDo(document("login",
                        requestFields(
                                fieldWithPath("login_id").description("로그인 아이디"),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("grant_type").description("토큰 인증 유형"),
                                fieldWithPath("access_token").description("액세스 토큰"),
                                fieldWithPath("access_token.token_type").description("토큰 유형"),
                                fieldWithPath("access_token.value").description("토큰 값"),
                                fieldWithPath("access_token.expired_at").description("토큰 만료일"),
                                fieldWithPath("refresh_token").description("리프레쉬 토큰"),
                                fieldWithPath("refresh_token.token_type").description("토큰 유형"),
                                fieldWithPath("refresh_token.value").description("토큰 값"),
                                fieldWithPath("refresh_token.expired_at").description("토큰 만료일")
                        )));

    }

    @Test
    void refreshToken_test() throws Exception {

        // given
        long now = System.currentTimeMillis();
        Token accessToken = new Jwt(TokenType.ACCESS, "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBQ0NFU1MiLCJpYXQiOjE2NzUyMTA4NzksImV4cCI6MTY3NTIxMTc3OSwidXNlcklkIjoxLCJyb2xlIjoiVVNFUiJ9.X1AfxGWGUPhC5ovt3hcLv8_6Zb8H0Z4yn8tDxHohrTx_kcgTDWIHPt8yDuTHYo9KmqqqIwTQ7VEtMaVyJdqKrQ", TokenType.ACCESS.getExpiresInMills() + now);
        Token refreshToken = new Jwt(TokenType.REFRESH, "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBQ0NFU1MiLCJpYXQiOjE2NzUyMTA4NzksImV4cCI6MTY3NTIxMTc3OSwidXNlcklkIjoxLCJyb2xlIjoiVVNFUiJ9.X1AfxGWGUPhC5ovt3hcLv8_6Zb8H0Z4yn8tDxHohrTx_kcgTDWIHPt8yDuTHYo9KmqqqIwTQ7VEtMaVyJdqKrQ", TokenType.REFRESH.getExpiresInMills() + now);
        TokenRefreshInfo response = new TokenRefreshInfo(GrantType.BEARER.getType(), accessToken);

        given(authService.refreshToken(refreshToken.getValue()))
                .willReturn(response);

        // when
        var result = mvc.perform(post("/api/auth/refresh-token")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + refreshToken.getValue())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grant_type").value(GrantType.BEARER.getType()))
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.access_token.token_type").value(TokenType.ACCESS.name()))
                .andExpect(jsonPath("$.access_token.value").value(accessToken.getValue()))
                .andExpect(jsonPath("$.access_token.expired_at").value(accessToken.getExpiredAt()))
                .andDo(print());

        then(authService).should().refreshToken(refreshToken.getValue());

        // API Docs
        result
                .andDo(document("refresh-token",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("리프레쉬 토큰")
                        ),
                        responseFields(
                                fieldWithPath("grant_type").description("토큰 인증 유형"),
                                fieldWithPath("access_token").description("액세스 토큰"),
                                fieldWithPath("access_token.token_type").description("토큰 유형"),
                                fieldWithPath("access_token.value").description("토큰 값"),
                                fieldWithPath("access_token.expired_at").description("토큰 만료일")
                        )));

    }

    @Test
    void findAccount_test() throws Exception {

        // given
        String email = "test@gmail.com";
        FindAccount request = new FindAccount(email);

        doNothing().when(authService).findAccount(request);

        // when
        var result = mvc.perform(post("/api/auth/find-account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(request)));

        // then
        result
                .andExpect(status().isNoContent())
                .andDo(print());

        then(authService).should().findAccount(request);

        // API Docs
        result
                .andDo(document("find-account",
                        requestFields(
                                fieldWithPath("email").description("이메일")
                        )));
    }

}