package com.leesh.devlab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leesh.devlab.domain.member.OauthType;
import com.leesh.devlab.dto.*;
import com.leesh.devlab.jwt.GrantType;
import com.leesh.devlab.jwt.Token;
import com.leesh.devlab.jwt.TokenType;
import com.leesh.devlab.jwt.implementation.Jwt;
import com.leesh.devlab.service.AuthService;
import com.leesh.devlab.service.MemberService;
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
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @MockBean
    private MemberService memberService;

    @Test
    void oauthLogin_test() throws Exception {

        // given
        String authorizationCode = "y7iyuzOxjD3AnPOtNDkxlKhVEtdjIBduM7uJboWnDskFxrD9GvitLQpqpnA7fAc4pMvowAo9dJcAAAGGCllssw";
        Token accessToken = new Jwt(TokenType.ACCESS, "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBQ0NFU1MiLCJpYXQiOjE2NzUyMTA4NzksImV4cCI6MTY3NTIxMTc3OSwidXNlcklkIjoxLCJyb2xlIjoiVVNFUiJ9.X1AfxGWGUPhC5ovt3hcLv8_6Zb8H0Z4yn8tDxHohrTx_kcgTDWIHPt8yDuTHYo9KmqqqIwTQ7VEtMaVyJdqKrQ", TokenType.ACCESS.getExpiresIn());
        Token refreshToken = new Jwt(TokenType.REFRESH, "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBQ0NFU1MiLCJpYXQiOjE2NzUyMTA4NzksImV4cCI6MTY3NTIxMTc3OSwidXNlcklkIjoxLCJyb2xlIjoiVVNFUiJ9.X1AfxGWGUPhC5ovt3hcLv8_6Zb8H0Z4yn8tDxHohrTx_kcgTDWIHPt8yDuTHYo9KmqqqIwTQ7VEtMaVyJdqKrQ", TokenType.REFRESH.getExpiresIn());

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
                .andExpect(cookie().value("refresh_token", refreshToken.getValue()))
                .andExpect(cookie().maxAge("refresh_token", refreshToken.getExpiresIn()))
                .andExpect(jsonPath("$.grant_type").value(GrantType.BEARER.getType()))
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.access_token.token_type").value(TokenType.ACCESS.name()))
                .andExpect(jsonPath("$.access_token.value").value(accessToken.getValue()))
                .andExpect(jsonPath("$.access_token.expires_in").value(accessToken.getExpiresIn()))
                .andExpect(jsonPath("$.refresh_token").exists())
                .andExpect(jsonPath("$.refresh_token.token_type").value(TokenType.REFRESH.name()))
                .andExpect(jsonPath("$.refresh_token.value").value(refreshToken.getValue()))
                .andExpect(jsonPath("$.refresh_token.expires_in").value(refreshToken.getExpiresIn()))
                .andDo(print());

        then(authService).should().oauthLogin(request);

        // API Docs
        result
                .andDo(document("oauth-login",
                        requestFields(
                                fieldWithPath("oauth_type").description("소셜 계정 유형"),
                                fieldWithPath("authorization_code").description("인가 코드")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.SET_COOKIE).description("클라이언트 쿠키에 설정할 리프레시 토큰 (Http Only)")
                        ),
                        responseFields(
                                fieldWithPath("grant_type").description("토큰 인증 유형"),
                                fieldWithPath("access_token").description("액세스 토큰"),
                                fieldWithPath("access_token.token_type").description("토큰 유형"),
                                fieldWithPath("access_token.value").description("토큰 값"),
                                fieldWithPath("access_token.expires_in").description("토큰 만료일"),
                                fieldWithPath("refresh_token").description("리프레쉬 토큰"),
                                fieldWithPath("refresh_token.token_type").description("토큰 유형"),
                                fieldWithPath("refresh_token.value").description("토큰 값"),
                                fieldWithPath("refresh_token.expires_in").description("토큰 만료일")
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

        Token accessToken = new Jwt(TokenType.ACCESS, "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBQ0NFU1MiLCJpYXQiOjE2NzUyMTA4NzksImV4cCI6MTY3NTIxMTc3OSwidXNlcklkIjoxLCJyb2xlIjoiVVNFUiJ9.X1AfxGWGUPhC5ovt3hcLv8_6Zb8H0Z4yn8tDxHohrTx_kcgTDWIHPt8yDuTHYo9KmqqqIwTQ7VEtMaVyJdqKrQ", TokenType.ACCESS.getExpiresIn());
        Token refreshToken = new Jwt(TokenType.REFRESH, "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBQ0NFU1MiLCJpYXQiOjE2NzUyMTA4NzksImV4cCI6MTY3NTIxMTc3OSwidXNlcklkIjoxLCJyb2xlIjoiVVNFUiJ9.X1AfxGWGUPhC5ovt3hcLv8_6Zb8H0Z4yn8tDxHohrTx_kcgTDWIHPt8yDuTHYo9KmqqqIwTQ7VEtMaVyJdqKrQ", TokenType.REFRESH.getExpiresIn());
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
                .andExpect(cookie().value("refresh_token", refreshToken.getValue()))
                .andExpect(cookie().maxAge("refresh_token", refreshToken.getExpiresIn()))
                .andExpect(jsonPath("$.grant_type").value(GrantType.BEARER.getType()))
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.access_token.token_type").value(TokenType.ACCESS.name()))
                .andExpect(jsonPath("$.access_token.value").value(accessToken.getValue()))
                .andExpect(jsonPath("$.access_token.expires_in").value(accessToken.getExpiresIn()))
                .andExpect(jsonPath("$.refresh_token").exists())
                .andExpect(jsonPath("$.refresh_token.token_type").value(TokenType.REFRESH.name()))
                .andExpect(jsonPath("$.refresh_token.value").value(refreshToken.getValue()))
                .andExpect(jsonPath("$.refresh_token.expires_in").value(refreshToken.getExpiresIn()))
                .andDo(print());

        then(authService).should().login(request);

        // API Docs
        result
                .andDo(document("login",
                        requestFields(
                                fieldWithPath("login_id").description("로그인 아이디"),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.SET_COOKIE).description("클라이언트 쿠키에 설정할 리프레시 토큰 (Http Only)")
                        ),
                        responseFields(
                                fieldWithPath("grant_type").description("토큰 인증 유형"),
                                fieldWithPath("access_token").description("액세스 토큰"),
                                fieldWithPath("access_token.token_type").description("토큰 유형"),
                                fieldWithPath("access_token.value").description("토큰 값"),
                                fieldWithPath("access_token.expires_in").description("토큰 만료일"),
                                fieldWithPath("refresh_token").description("리프레쉬 토큰"),
                                fieldWithPath("refresh_token.token_type").description("토큰 유형"),
                                fieldWithPath("refresh_token.value").description("토큰 값"),
                                fieldWithPath("refresh_token.expires_in").description("토큰 만료일")
                        )));

    }

    @Test
    void refreshToken_test() throws Exception {

        // given
        Token accessToken = new Jwt(TokenType.ACCESS, "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBQ0NFU1MiLCJpYXQiOjE2NzUyMTA4NzksImV4cCI6MTY3NTIxMTc3OSwidXNlcklkIjoxLCJyb2xlIjoiVVNFUiJ9.X1AfxGWGUPhC5ovt3hcLv8_6Zb8H0Z4yn8tDxHohrTx_kcgTDWIHPt8yDuTHYo9KmqqqIwTQ7VEtMaVyJdqKrQ", TokenType.ACCESS.getExpiresIn());
        Token refreshToken = new Jwt(TokenType.REFRESH, "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBQ0NFU1MiLCJpYXQiOjE2NzUyMTA4NzksImV4cCI6MTY3NTIxMTc3OSwidXNlcklkIjoxLCJyb2xlIjoiVVNFUiJ9.X1AfxGWGUPhC5ovt3hcLv8_6Zb8H0Z4yn8tDxHohrTx_kcgTDWIHPt8yDuTHYo9KmqqqIwTQ7VEtMaVyJdqKrQ", TokenType.REFRESH.getExpiresIn());
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
                .andExpect(jsonPath("$.access_token.expires_in").value(accessToken.getExpiresIn()))
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
                                fieldWithPath("access_token.expires_in").description("토큰 만료일")
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

    @Test
    void checkId_test() throws Exception {

        // given
        String loginId = "test";

        doNothing().when(memberService).validateLoginId(loginId);

        // when
        var result = mvc.perform(get("/api/auth/id-checks")
                .param("id", loginId));

        // then
        result
                .andExpect(status().isNoContent())
                .andDo(print());

        then(memberService).should().validateLoginId(loginId);

        // API Docs
        result
                .andDo(document("check-id",
                        queryParameters(
                                parameterWithName("id").description("검증할 ID")
                        )));
    }

    @Test
    void checkNickname_test() throws Exception {

        // given
        String nickname = "test";

        doNothing().when(memberService).validateNickname(nickname);

        // when
        var result = mvc.perform(get("/api/auth/nickname-checks")
                .param("nickname", nickname));

        // then
        result
                .andExpect(status().isNoContent())
                .andDo(print());

        then(memberService).should().validateNickname(nickname);

        // API Docs
        result
                .andDo(document("check-nickname",
                        queryParameters(
                                parameterWithName("nickname").description("검증할 닉네임")
                        )));
    }


}