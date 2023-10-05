package com.leesh.devlab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leesh.devlab.domain.member.Role;
import com.leesh.devlab.dto.LikeInfo;
import com.leesh.devlab.jwt.Token;
import com.leesh.devlab.jwt.TokenService;
import com.leesh.devlab.jwt.TokenType;
import com.leesh.devlab.jwt.dto.LoginInfo;
import com.leesh.devlab.jwt.implementation.Jwt;
import com.leesh.devlab.service.CommentService;
import com.leesh.devlab.service.LikeService;
import com.leesh.devlab.service.PostService;
import config.WebMvcTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@Import(WebMvcTestConfig.class)
@AutoConfigureRestDocs
class CommentControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private PostService postService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private LikeService likeService;

    private Token accessToken;

    private LoginInfo testMember;

    @BeforeEach
    void setUp() {

        accessToken = new Jwt(TokenType.ACCESS, "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBQ0NFU1MiLCJpYXQiOjE2NzUyMTA4NzksImV4cCI6MTY3NTIxMTc3OSwidXNlcklkIjoxLCJyb2xlIjoiVVNFUiJ9.X1AfxGWGUPhC5ovt3hcLv8_6Zb8H0Z4yn8tDxHohrTx_kcgTDWIHPt8yDuTHYo9KmqqqIwTQ7VEtMaVyJdqKrQ", TokenType.ACCESS.getExpiresInMills() + System.currentTimeMillis());
        testMember = new LoginInfo(1L, "test", Role.MEMBER);

        doNothing().when(tokenService).validateToken(accessToken.getValue(), accessToken.getTokenType());
        given(tokenService.extractLoginInfo(accessToken.getValue()))
                .willReturn(testMember);

    }

    @Test
    void delete_test() throws Exception {

        // given
        long commentId = 1L;
        doNothing().when(commentService).delete(commentId, testMember);

        // when
        var result = mvc.perform(delete("/api/comments/{comment-id}", commentId)
                .header("Authorization", "Bearer " + accessToken.getValue()));

        // then
        result
                .andExpect(status().isNoContent())
                .andDo(print());

        then(commentService).should().delete(commentId, testMember);

        // API Docs
        result
                .andDo(document("comments/delete",
                        requestHeaders(
                                headerWithName("Authorization").description("접근 토큰(Access Token)")
                        ),
                        pathParameters(
                                parameterWithName("comment-id").description("댓글 ID")
                        )
                ));
    }

    @Test
    void createLike_test() throws Exception {

        // given
        long commentId = 1L;
        long likeId = 1L;
        long now = System.currentTimeMillis();
        LikeInfo response = new LikeInfo(likeId, now, now);
        given(likeService.createCommentLike(testMember, commentId))
                .willReturn(response);

        // when
        var result = mvc.perform(post("/api/comments/{comment-id}/likes", commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken.getValue()));

        // then
        result
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.like_id").value(likeId))
                .andExpect(jsonPath("$.created_at").value(now))
                .andExpect(jsonPath("$.modified_at").value(now))
                .andDo(print());

        then(likeService).should().createCommentLike(testMember, commentId);

        // API Docs
        result.andDo(document("comments/create-like",
                requestHeaders(
                        headerWithName("Authorization").description("액세스 토큰")
                ),
                pathParameters(
                        parameterWithName("comment-id").description("댓글 식별자")
                ),
                responseFields(
                        fieldWithPath("like_id").description("좋아요 식별자"),
                        fieldWithPath("created_at").description("생성일"),
                        fieldWithPath("modified_at").description("수정일")
                )));
    }
}