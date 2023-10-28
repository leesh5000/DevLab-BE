package com.leesh.devlab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leesh.devlab.constant.GrantType;
import com.leesh.devlab.constant.OauthType;
import com.leesh.devlab.constant.Role;
import com.leesh.devlab.constant.TokenType;
import com.leesh.devlab.constant.dto.*;
import com.leesh.devlab.jwt.Token;
import com.leesh.devlab.jwt.implementation.Jwt;
import com.leesh.devlab.service.AuthService;
import com.leesh.devlab.service.CookieService;
import com.leesh.devlab.service.MailService;
import com.leesh.devlab.service.MemberService;
import config.WebMvcTestConfig;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.leesh.devlab.service.CookieService.COOKIE_DOMAIN;
import static com.leesh.devlab.service.CookieService.encode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.cookies.CookieDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
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

    @MockBean
    private CookieService cookieService;

    @MockBean
    private MailService mailService;

    @Test
    void oauthLogin_test() throws Exception {

        // given
        String authorizationCode = "y7iyuzOxjD3AnPOtNDkxlKhVEtdjIBduM7uJboWnDskFxrD9GvitLQpqpnA7fAc4pMvowAo9dJcAAAGGCllssw";
        Token accessToken = new Jwt(TokenType.ACCESS, "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBQ0NFU1MiLCJpYXQiOjE2NzUyMTA4NzksImV4cCI6MTY3NTIxMTc3OSwidXNlcklkIjoxLCJyb2xlIjoiVVNFUiJ9.X1AfxGWGUPhC5ovt3hcLv8_6Zb8H0Z4yn8tDxHohrTx_kcgTDWIHPt8yDuTHYo9KmqqqIwTQ7VEtMaVyJdqKrQ", TokenType.ACCESS.getExpiresInSeconds());
        Token refreshToken = new Jwt(TokenType.REFRESH, "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBQ0NFU1MiLCJpYXQiOjE2NzUyMTA4NzksImV4cCI6MTY3NTIxMTc3OSwidXNlcklkIjoxLCJyb2xlIjoiVVNFUiJ9.X1AfxGWGUPhC5ovt3hcLv8_6Zb8H0Z4yn8tDxHohrTx_kcgTDWIHPt8yDuTHYo9KmqqqIwTQ7VEtMaVyJdqKrQ", TokenType.REFRESH.getExpiresInSeconds());

        TokenInfoDto tokenInfoDto = new TokenInfoDto(GrantType.BEARER.getType(), accessToken, refreshToken);
        UserInfoDto userInfoDto = new UserInfoDto(1L, "test", Role.MEMBER);
        OauthLoginRequestDto requestDto = new OauthLoginRequestDto(OauthType.NAVER, authorizationCode);
        LoginResponseDto responseDto = new LoginResponseDto(tokenInfoDto, userInfoDto);

        ResponseCookie responseCookie = ResponseCookie.from(responseDto.tokenInfo().refreshToken().getTokenType().name(), responseDto.tokenInfo().refreshToken().getValue())
                .httpOnly(true)
                .domain(COOKIE_DOMAIN)
                .sameSite("None")
                .secure(true)
                .path("/")
                .maxAge(responseDto.tokenInfo().refreshToken().getExpiresInSeconds())
                .build();

        given(authService.oauthLogin(requestDto))
                .willReturn(responseDto);

        given(cookieService.generateCookie(any(String.class), any(String.class), any(Integer.class)))
                .willReturn(responseCookie);

        // when
        ResultActions result = mvc.perform(post("/api/auth/oauth-login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(requestDto)));

        // then
        result
                .andExpect(status().isOk())
                .andExpect(cookie().value(responseDto.tokenInfo().refreshToken().getTokenType().name(), responseDto.tokenInfo().refreshToken().getValue()))
                .andExpect(cookie().maxAge(responseDto.tokenInfo().refreshToken().getTokenType().name(), responseDto.tokenInfo().refreshToken().getExpiresInSeconds()))
                .andExpect(cookie().domain(responseDto.tokenInfo().refreshToken().getTokenType().name(), COOKIE_DOMAIN))
                .andExpect(cookie().httpOnly(responseDto.tokenInfo().refreshToken().getTokenType().name(), true))
                .andExpect(jsonPath("$.user_info").exists())
                .andExpect(jsonPath("$.user_info.id").value(userInfoDto.id()))
                .andExpect(jsonPath("$.user_info.nickname").value(userInfoDto.nickname()))
                .andExpect(jsonPath("$.user_info.role").value(userInfoDto.role().name()))
                .andExpect(jsonPath("$.token_info").exists())
                .andExpect(jsonPath("$.token_info.grant_type").value(tokenInfoDto.grantType()))
                .andExpect(jsonPath("$.token_info.access_token").exists())
                .andExpect(jsonPath("$.token_info.access_token.token_type").value(TokenType.ACCESS.name()))
                .andExpect(jsonPath("$.token_info.access_token.value").value(accessToken.getValue()))
                .andExpect(jsonPath("$.token_info.access_token.expires_in_seconds").value(accessToken.getExpiresInSeconds()))
                .andExpect(jsonPath("$.token_info.refresh_token").exists())
                .andExpect(jsonPath("$.token_info.refresh_token.token_type").value(TokenType.REFRESH.name()))
                .andExpect(jsonPath("$.token_info.refresh_token.value").value(refreshToken.getValue()))
                .andExpect(jsonPath("$.token_info.refresh_token.expires_in_seconds").value(refreshToken.getExpiresInSeconds()))
                .andDo(print());

        then(authService).should().oauthLogin(requestDto);

        // API Docs
        result
                .andDo(document("oauth-login",
                        requestFields(
                                fieldWithPath("oauth_type").description("소셜 계정 유형"),
                                fieldWithPath("authorization_code").description("인가 코드")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.SET_COOKIE).description("클라이언트에 쿠키 생성")
                        ),
                        responseCookies(
                                cookieWithName(TokenType.REFRESH.name()).description("리프레시 토큰 (Http Only)")
                        ),
                        responseFields(
                                fieldWithPath("user_info").description("현재 로그인 한 유저 정보"),
                                fieldWithPath("user_info.id").description("유저의 식별자 ID"),
                                fieldWithPath("user_info.nickname").description("닉네임"),
                                fieldWithPath("user_info.role").description("유저의 권한"),
                                fieldWithPath("token_info").description("토큰 정보"),
                                fieldWithPath("token_info.grant_type").description("토큰 인증 유형"),
                                fieldWithPath("token_info.access_token").description("액세스 토큰"),
                                fieldWithPath("token_info.access_token.token_type").description("토큰 유형"),
                                fieldWithPath("token_info.access_token.value").description("토큰 값"),
                                fieldWithPath("token_info.access_token.expires_in_seconds").description("토큰 만료 시간 (초 단위)"),
                                fieldWithPath("token_info.refresh_token").description("리프레시 토큰"),
                                fieldWithPath("token_info.refresh_token.token_type").description("토큰 유형"),
                                fieldWithPath("token_info.refresh_token.value").description("토큰 값"),
                                fieldWithPath("token_info.refresh_token.expires_in_seconds").description("토큰 만료 시간 (초 단위)")
                        )));

    }

    @Test
    void register_test() throws Exception {

        // given
        EmailDto emailDto = new EmailDto("name@company.com", false);
        RegisterRequestDto requestDto = new RegisterRequestDto("test", "test", "test", emailDto);

        given(authService.register(requestDto))
                .willReturn(new RegisterResponseDto(1L));

        // when
        var result = mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(requestDto)));

        // then
        result
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.member_id").exists())
                .andDo(print());

        then(authService).should().register(requestDto);

        // API Docs
        result
                .andDo(document("register",
                        requestFields(
                                fieldWithPath("login_id").description("로그인 아이디"),
                                fieldWithPath("password").description("비밀번호"),
                                fieldWithPath("nickname").description("닉네임"),
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("email.address").description("이메일 주소"),
                                fieldWithPath("email.verified").description("이메일 인증 여부")
                        ),
                        responseFields(
                                fieldWithPath("member_id").description("생성된 회원 아이디(PK)")
                        )));
    }

    @Test
    void login_test() throws Exception {

        // given
        Token accessToken = new Jwt(TokenType.ACCESS, "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBQ0NFU1MiLCJpYXQiOjE2NzUyMTA4NzksImV4cCI6MTY3NTIxMTc3OSwidXNlcklkIjoxLCJyb2xlIjoiVVNFUiJ9.X1AfxGWGUPhC5ovt3hcLv8_6Zb8H0Z4yn8tDxHohrTx_kcgTDWIHPt8yDuTHYo9KmqqqIwTQ7VEtMaVyJdqKrQ", TokenType.ACCESS.getExpiresInSeconds());
        Token refreshToken = new Jwt(TokenType.REFRESH, "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBQ0NFU1MiLCJpYXQiOjE2NzUyMTA4NzksImV4cCI6MTY3NTIxMTc3OSwidXNlcklkIjoxLCJyb2xlIjoiVVNFUiJ9.X1AfxGWGUPhC5ovt3hcLv8_6Zb8H0Z4yn8tDxHohrTx_kcgTDWIHPt8yDuTHYo9KmqqqIwTQ7VEtMaVyJdqKrQ", TokenType.REFRESH.getExpiresInSeconds());
        String loginId = "test";
        String password = "test";
        long id = 1L;

        LoginRequestDto requestDto = new LoginRequestDto(loginId, password);
        UserInfoDto userInfoDto = new UserInfoDto(id, loginId, Role.MEMBER);
        TokenInfoDto tokenInfoDto = new TokenInfoDto(GrantType.BEARER.getType(), accessToken, refreshToken);
        LoginResponseDto responseDto = new LoginResponseDto(tokenInfoDto, userInfoDto);

        ResponseCookie responseCookie = ResponseCookie.from(responseDto.tokenInfo().refreshToken().getTokenType().name(), responseDto.tokenInfo().refreshToken().getValue())
                .httpOnly(true)
                .domain(COOKIE_DOMAIN)
                .sameSite("None")
                .secure(true)
                .path("/")
                .maxAge(responseDto.tokenInfo().refreshToken().getExpiresInSeconds())
                .build();

        given(authService.login(requestDto))
                .willReturn(responseDto);

        given(cookieService.generateCookie(any(String.class), any(String.class), any(Integer.class)))
                .willReturn(responseCookie);

        // when
        var result = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(requestDto)));

        // then
        result
                .andExpect(status().isOk())
                .andExpect(cookie().value(responseDto.tokenInfo().refreshToken().getTokenType().name(), responseDto.tokenInfo().refreshToken().getValue()))
                .andExpect(cookie().maxAge(responseDto.tokenInfo().refreshToken().getTokenType().name(), responseDto.tokenInfo().refreshToken().getExpiresInSeconds()))
                .andExpect(cookie().domain(responseDto.tokenInfo().refreshToken().getTokenType().name(), COOKIE_DOMAIN))
                .andExpect(cookie().httpOnly(responseDto.tokenInfo().refreshToken().getTokenType().name(), true))
                .andExpect(jsonPath("$.user_info").exists())
                .andExpect(jsonPath("$.user_info.id").value(userInfoDto.id()))
                .andExpect(jsonPath("$.user_info.nickname").value(userInfoDto.nickname()))
                .andExpect(jsonPath("$.user_info.role").value(userInfoDto.role().name()))
                .andExpect(jsonPath("$.token_info").exists())
                .andExpect(jsonPath("$.token_info.grant_type").value(tokenInfoDto.grantType()))
                .andExpect(jsonPath("$.token_info.access_token").exists())
                .andExpect(jsonPath("$.token_info.access_token.token_type").value(TokenType.ACCESS.name()))
                .andExpect(jsonPath("$.token_info.access_token.value").value(accessToken.getValue()))
                .andExpect(jsonPath("$.token_info.access_token.expires_in_seconds").value(accessToken.getExpiresInSeconds()))
                .andExpect(jsonPath("$.token_info.refresh_token").exists())
                .andExpect(jsonPath("$.token_info.refresh_token.token_type").value(TokenType.REFRESH.name()))
                .andExpect(jsonPath("$.token_info.refresh_token.value").value(refreshToken.getValue()))
                .andExpect(jsonPath("$.token_info.refresh_token.expires_in_seconds").value(refreshToken.getExpiresInSeconds()))
                .andDo(print());

        then(authService).should().login(requestDto);

        // API Docs
        result
                .andDo(document("login",
                        requestFields(
                                fieldWithPath("login_id").description("로그인 아이디"),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.SET_COOKIE).description("클라이언트에 쿠키 생성")
                        ),
                        responseCookies(
                                cookieWithName(TokenType.REFRESH.name()).description("리프레시 토큰 (Http Only)")
                        ),
                        responseFields(
                                fieldWithPath("user_info").description("현재 로그인 한 유저 정보"),
                                fieldWithPath("user_info.id").description("유저의 식별자 ID"),
                                fieldWithPath("user_info.nickname").description("닉네임"),
                                fieldWithPath("user_info.role").description("유저의 권한"),
                                fieldWithPath("token_info").description("토큰 정보"),
                                fieldWithPath("token_info.grant_type").description("토큰 인증 유형"),
                                fieldWithPath("token_info.access_token").description("액세스 토큰"),
                                fieldWithPath("token_info.access_token.token_type").description("토큰 유형"),
                                fieldWithPath("token_info.access_token.value").description("토큰 값"),
                                fieldWithPath("token_info.access_token.expires_in_seconds").description("토큰 만료 시간 (초 단위)"),
                                fieldWithPath("token_info.refresh_token").description("리프레시 토큰"),
                                fieldWithPath("token_info.refresh_token.token_type").description("토큰 유형"),
                                fieldWithPath("token_info.refresh_token.value").description("토큰 값"),
                                fieldWithPath("token_info.refresh_token.expires_in_seconds").description("토큰 만료 시간 (초 단위)")
                        )));

    }

    @Test
    void logout_test() throws Exception {

        // given
        Token refreshToken = new Jwt(TokenType.REFRESH, "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBQ0NFU1MiLCJpYXQiOjE2NzUyMTA4NzksImV4cCI6MTY3NTIxMTc3OSwidXNlcklkIjoxLCJyb2xlIjoiVVNFUiJ9.X1AfxGWGUPhC5ovt3hcLv8_6Zb8H0Z4yn8tDxHohrTx_kcgTDWIHPt8yDuTHYo9KmqqqIwTQ7VEtMaVyJdqKrQ", TokenType.REFRESH.getExpiresInSeconds());
        Cookie cookie = new Cookie(refreshToken.getTokenType().name(), refreshToken.getValue());

        ResponseCookie responseCookie = ResponseCookie.from(TokenType.REFRESH.name(), "")
                .httpOnly(true)
                .domain(COOKIE_DOMAIN)
                .sameSite("None")
                .secure(true)
                .path("/")
                .maxAge((0))
                .build();

        doNothing().when(authService).logout(refreshToken.getValue());
        given(cookieService.extractCookies(any(HttpServletRequest.class), eq(TokenType.REFRESH.name())))
                .willReturn(cookie);

        given(cookieService.generateCookie(eq(TokenType.REFRESH.name()), any(String.class), eq(0)))
                .willReturn(responseCookie);

        // when
        var result = mvc.perform(delete("/api/auth/logout")
                .header(HttpHeaders.COOKIE, cookie.toString())
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result
                .andExpect(status().isNoContent())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, "REFRESH=; Path=/; Domain=devlab.com; Max-Age=0; Expires=Thu, 1 Jan 1970 00:00:00 GMT; Secure; HttpOnly; SameSite=None"))
                .andExpect(cookie().value(TokenType.REFRESH.name(), ""))
                .andExpect(cookie().maxAge(TokenType.REFRESH.name(), 0))
                .andExpect(cookie().domain(TokenType.REFRESH.name(), COOKIE_DOMAIN))
                .andExpect(cookie().httpOnly(TokenType.REFRESH.name(), true))
                .andDo(print());

        then(authService).should().logout(refreshToken.getValue());


        // API Docs
        result
                .andDo(document("logout",
                        requestCookies(
                                cookieWithName(TokenType.REFRESH.name()).description("리프레시 토큰 (Http Only)")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.SET_COOKIE).description("클라이언트에 쿠키를 설정")
                        ),
                        responseCookies(
                                cookieWithName(TokenType.REFRESH.name()).description("클라이언트의 쿠키를 삭제 (maxAge = 0)"
                        ))
                ));
    }

    @Test
    void refreshToken_test() throws Exception {

        // given
        Token accessToken = new Jwt(TokenType.ACCESS, "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBQ0NFU1MiLCJpYXQiOjE2NzUyMTA4NzksImV4cCI6MTY3NTIxMTc3OSwidXNlcklkIjoxLCJyb2xlIjoiVVNFUiJ9.X1AfxGWGUPhC5ovt3hcLv8_6Zb8H0Z4yn8tDxHohrTx_kcgTDWIHPt8yDuTHYo9KmqqqIwTQ7VEtMaVyJdqKrQ", TokenType.ACCESS.getExpiresInSeconds());
        Token refreshToken = new Jwt(TokenType.REFRESH, "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBQ0NFU1MiLCJpYXQiOjE2NzUyMTA4NzksImV4cCI6MTY3NTIxMTc3OSwidXNlcklkIjoxLCJyb2xlIjoiVVNFUiJ9.X1AfxGWGUPhC5ovt3hcLv8_6Zb8H0Z4yn8tDxHohrTx_kcgTDWIHPt8yDuTHYo9KmqqqIwTQ7VEtMaVyJdqKrQ", TokenType.REFRESH.getExpiresInSeconds());
        Long id = 1L;
        String loginId = "test1";
        String nickname = "test1";
        TokenInfoDto tokenInfoDto = new TokenInfoDto(GrantType.BEARER.getType(), accessToken, null);
        UserInfoDto userInfoDto = new UserInfoDto(id, loginId, Role.MEMBER);
        LoginResponseDto responseDto = new LoginResponseDto(tokenInfoDto, userInfoDto);

        String requestCookie = ResponseCookie.from(refreshToken.getTokenType().name(), refreshToken.getValue())
                .httpOnly(true)
                .domain(COOKIE_DOMAIN)
                .sameSite("None")
                .secure(true)
                .path("/")
                .maxAge(refreshToken.getExpiresInSeconds())
                .build().getValue();

        Cookie cookie = new Cookie(TokenType.REFRESH.name(), requestCookie);

        given(cookieService.extractCookies(any(HttpServletRequest.class), eq(TokenType.REFRESH.name())))
                .willReturn(cookie);

        given(authService.refreshToken(refreshToken.getValue()))
                .willReturn(responseDto);

        // when
        var result = mvc.perform(post("/api/auth/refresh-token")
                .header(HttpHeaders.COOKIE, cookie.getValue())
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_info").exists())
                .andExpect(jsonPath("$.user_info.id").value(userInfoDto.id()))
                .andExpect(jsonPath("$.user_info.nickname").value(userInfoDto.nickname()))
                .andExpect(jsonPath("$.user_info.role").value(userInfoDto.role().name()))
                .andExpect(jsonPath("$.token_info").exists())
                .andExpect(jsonPath("$.token_info.grant_type").value(tokenInfoDto.grantType()))
                .andExpect(jsonPath("$.token_info.refresh_token").doesNotExist())
                .andExpect(jsonPath("$.token_info.access_token").exists())
                .andExpect(jsonPath("$.token_info.access_token.token_type").value(TokenType.ACCESS.name()))
                .andExpect(jsonPath("$.token_info.access_token.value").value(accessToken.getValue()))
                .andExpect(jsonPath("$.token_info.access_token.expires_in_seconds").value(accessToken.getExpiresInSeconds()))
                .andDo(print());

        then(authService).should().refreshToken(refreshToken.getValue());

        // API Docs
        result
                .andDo(document("refresh-token",
                        requestCookies(
                                cookieWithName(TokenType.REFRESH.name()).description("리프레시 토큰 (Http Only)")
                        ),
                        responseFields(
                                fieldWithPath("user_info").description("현재 로그인 한 유저 정보"),
                                fieldWithPath("user_info.id").description("유저의 식별자 ID"),
                                fieldWithPath("user_info.nickname").description("닉네임"),
                                fieldWithPath("user_info.role").description("유저의 권한"),
                                fieldWithPath("token_info").description("토큰 정보"),
                                fieldWithPath("token_info.grant_type").description("토큰 인증 유형"),
                                fieldWithPath("token_info.refresh_token").description("리프레시 토큰 (NULL)"),
                                fieldWithPath("token_info.access_token").description("액세스 토큰"),
                                fieldWithPath("token_info.access_token.token_type").description("토큰 유형"),
                                fieldWithPath("token_info.access_token.value").description("토큰 값"),
                                fieldWithPath("token_info.access_token.expires_in_seconds").description("토큰 만료 시간 (초 단위)")
                        )));
    }

    @Test
    void validateId_test() throws Exception {

        // given
        String loginId = "test";

        doNothing().when(memberService).checkLoginId(loginId);

        // when
        var result = mvc.perform(get("/api/auth/id-checks")
                .param("id", loginId));

        // then
        result
                .andExpect(status().isNoContent())
                .andDo(print());

        then(memberService).should().checkLoginId(loginId);

        // API Docs
        result
                .andDo(document("id-checks",
                        queryParameters(
                                parameterWithName("id").description("검증할 ID")
                        )));
    }

    @Test
    void validateNickname_test() throws Exception {

        // given
        String nickname = "test";

        doNothing().when(memberService).checkNickname(nickname);

        // when
        var result = mvc.perform(get("/api/auth/nickname-checks")
                .param("nickname", nickname));

        // then
        result
                .andExpect(status().isNoContent())
                .andDo(print());

        then(memberService).should().checkNickname(nickname);

        // API Docs
        result
                .andDo(document("nickname-checks",
                        queryParameters(
                                parameterWithName("nickname").description("검증할 닉네임")
                        )));
    }

    @Test
    void verifyEmail_test() throws Exception {

        // given
        String email = "test@gmail.com";
        String verificationCode = "123456";
        int maxAgeSeconds = 5 * 60;
        String encodedKey = encode(email);

        given(authService.generateVerificationCode()).willReturn(verificationCode);
        doNothing().when(mailService).sendMail(any(String.class), any(String.class), any(String.class));
        given(cookieService.generateCookie(any(String.class), any(String.class), any(Integer.class)))
                .willReturn(ResponseCookie.from(encodedKey, verificationCode)
                        .httpOnly(true)
                        .domain(COOKIE_DOMAIN)
                        .sameSite("None")
                        .secure(true)
                        .path("/")
                        .maxAge(maxAgeSeconds)
                        .build());

        // when
        var result = mvc.perform(get("/api/auth/email-verifications")
                .param("email", email));

        // then
        result
                .andExpect(status().isNoContent())
                .andExpect(cookie().value(encodedKey, verificationCode))
                .andExpect(cookie().maxAge(encodedKey, maxAgeSeconds))
                .andExpect(cookie().domain(encodedKey, COOKIE_DOMAIN))
                .andExpect(cookie().httpOnly(encodedKey, true))
                .andDo(print());

        then(authService).should().generateVerificationCode();
        then(mailService).should().sendMail(any(String.class), any(String.class), any(String.class));
        then(cookieService).should().generateCookie(any(String.class), any(String.class), any(Integer.class));

        // API Docs
        result
                .andDo(document("email-verifications",
                        queryParameters(
                                parameterWithName("email").description("인증 코드를 전송할 이메일")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.SET_COOKIE).description("인증코드 확인용 쿠키 (HttpOnly)")
                        )));
    }

    @Test
    void confirmEmail_test() throws Exception {

        // given
        String email = "test@gmail.com";
        String verificationCode = "123456";

        given(cookieService.extractCookies(any(HttpServletRequest.class), eq(email)))
                .willReturn(new Cookie(encode(email), verificationCode));

        // when
        var result = mvc.perform(get("/api/auth/email-confirms")
                .param("email", email)
                .param("code", verificationCode)
                .cookie(new Cookie(encode(email), verificationCode)
                ));

        // then
        result
                .andExpect(status().isNoContent())
                .andDo(print());

        then(cookieService).should().extractCookies(any(HttpServletRequest.class), eq(email));

        // API Docs
        result
                .andDo(document("email-confirms",
                        queryParameters(
                                parameterWithName("email").description("인증 코드를 전송한 이메일"),
                                parameterWithName("code").description("인증 코드")
                        ),
                        requestCookies(
                                cookieWithName(encode(email)).description("인증 코드 확인용 쿠키 (HttpOnly)")
                        )));
    }

    @Test
    void findLoginId_test() throws Exception {

        // given
        FindLoginIdRequestDto requestDto = new FindLoginIdRequestDto("71ff8973e3c8");
        FindLoginIdResponseDto responseDto = new FindLoginIdResponseDto("test10", OauthType.GOOGLE);

        given(authService.findLoginId(requestDto))
                .willReturn(responseDto);

        // when
        var result = mvc.perform(post("/api/auth/find-id")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsString(requestDto)));

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login_id").value(responseDto.loginId()))
                .andDo(print());

        then(authService).should().findLoginId(requestDto);

        // API Docs
        result
                .andDo(document("find-id",
                        requestFields(
                                fieldWithPath("security_code").description("보안 코드")
                        ),
                        responseFields(
                                fieldWithPath("login_id").description("로그인 아이디"),
                                fieldWithPath("oauth_type").description("소셜 계정 유형")
                        )));
    }

    @Test
    void checkSecurityCode_test() throws Exception {

        // given
        CheckSecurityCodeRequestDto requestDto = new CheckSecurityCodeRequestDto("test", "71ff8973e3c8");

        doNothing().when(authService).checkSecurityCode(requestDto);

        // when
        var result = mvc.perform(post("/api/auth/security-code-checks")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(requestDto)));

        // then
        result
                .andExpect(status().isNoContent())
                .andDo(print());

        then(authService).should().checkSecurityCode(requestDto);

        // API Docs
        result
                .andDo(document("checkSecurityCode",
                        requestFields(
                                fieldWithPath("login_id").description("로그인 아이디"),
                                fieldWithPath("security_code").description("보안 코드")
                        )));
    }
}