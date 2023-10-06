package com.leesh.devlab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leesh.devlab.domain.member.Role;
import com.leesh.devlab.domain.post.Category;
import com.leesh.devlab.dto.*;
import com.leesh.devlab.jwt.GrantType;
import com.leesh.devlab.jwt.Token;
import com.leesh.devlab.jwt.TokenService;
import com.leesh.devlab.jwt.TokenType;
import com.leesh.devlab.jwt.dto.LoginInfo;
import com.leesh.devlab.jwt.implementation.Jwt;
import com.leesh.devlab.service.CommentService;
import com.leesh.devlab.service.MemberService;
import com.leesh.devlab.service.PostService;
import config.WebMvcTestConfig;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
@Import(WebMvcTestConfig.class)
@AutoConfigureRestDocs
public class MemberControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @MockBean
    private MemberService memberService;

    @MockBean
    private PostService postService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private TokenService tokenService;

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
    void getMyProfile_test() throws Exception {

        // given
        String loginId = "test";
        String email = "test@gmail.com";
        long createdAt = System.currentTimeMillis();
        int postCount = 11;
        int postLikeCount = 112;
        int commentCount = 22;
        int commentLikeCount = 32;

        Activities activities = new Activities(postCount, postLikeCount, commentCount, commentLikeCount);
        MyProfile response = new MyProfile(testMember.id(), loginId, testMember.nickname(), email, createdAt, activities);

        given(memberService.getMyProfile(testMember))
                .willReturn(response);

        // when
        var result = mvc.perform(get("/api/members/me")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, GrantType.BEARER.getType() + " " + accessToken.getValue()));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testMember.id()))
                .andExpect(jsonPath("$.login_id").value(loginId))
                .andExpect(jsonPath("$.nickname").value(testMember.nickname()))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.created_at").value(createdAt))
                .andExpect(jsonPath("$.activities.post_count").value(postCount))
                .andExpect(jsonPath("$.activities.post_like_count").value(postLikeCount))
                .andExpect(jsonPath("$.activities.comment_count").value(commentCount))
                .andExpect(jsonPath("$.activities.comment_like_count").value(commentLikeCount))
                .andDo(print());

        then(memberService).should().getMyProfile(testMember);

        // API Docs
        result.andDo(document("get-my-profile",
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                ),
                responseFields(
                        fieldWithPath("id").description("식별자"),
                        fieldWithPath("login_id").description("로그인 아이디"),
                        fieldWithPath("nickname").description("닉네임"),
                        fieldWithPath("email").description("이메일"),
                        fieldWithPath("created_at").description("생성일"),
                        fieldWithPath("activities.post_count").description("유저가 작성한 게시글 수"),
                        fieldWithPath("activities.post_like_count").description("유저가 작성한 게시글의 좋아요 수"),
                        fieldWithPath("activities.comment_count").description("유저가 작성한 댓글 수"),
                        fieldWithPath("activities.comment_like_count").description("유저가 작성한 댓글의 받은 좋아요 수")
                )
        ));
    }

    @Test
    void getProfile_test() throws Exception {

        // given
        Activities activities = new Activities(10, 1, 32, 13);
        MemberProfile response = new MemberProfile(testMember.id(), testMember.nickname(), System.currentTimeMillis(), activities);

        given(memberService.getMemberProfile(testMember.id()))
                .willReturn(response);

        // when
        var result = mvc.perform(get("/api/members/{member-id}", testMember.id())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, GrantType.BEARER.getType() + " " + accessToken.getValue()));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testMember.id()))
                .andExpect(jsonPath("$.nickname").value(testMember.nickname()))
                .andExpect(jsonPath("$.created_at").value(response.createdAt()))
                .andExpect(jsonPath("$.activities.post_count").value(response.activities().postCount()))
                .andExpect(jsonPath("$.activities.post_like_count").value(response.activities().postLikeCount()))
                .andExpect(jsonPath("$.activities.comment_count").value(response.activities().commentCount()))
                .andExpect(jsonPath("$.activities.comment_like_count").value(response.activities().commentLikeCount()))
                .andDo(print());

        then(memberService).should().getMemberProfile(testMember.id());

        // API Docs
        result.andDo(document("get-profile",
                pathParameters(
                        parameterWithName("member-id").description("유저 식별자")
                ),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                ),
                responseFields(
                        fieldWithPath("id").description("식별자"),
                        fieldWithPath("nickname").description("닉네임"),
                        fieldWithPath("created_at").description("생성일"),
                        fieldWithPath("activities.post_count").description("유저가 작성한 게시글 수"),
                        fieldWithPath("activities.post_like_count").description("유저가 작성한 게시글의 좋아요 수"),
                        fieldWithPath("activities.comment_count").description("유저가 작성한 댓글 수"),
                        fieldWithPath("activities.comment_like_count").description("유저가 작성한 댓글의 받은 좋아요 수")
                )
        ));
    }

    @Test
    void updateProfile_test() throws Exception {

        // given
        UpdateProfile request = new UpdateProfile("new-test", "new-test", "new-test@gmail.com");
        doNothing().when(memberService).updateProfile(testMember.id(), request);

        // when
        var result = mvc.perform(patch("/api/members/{member-id}", testMember.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, GrantType.BEARER.getType() + " " + accessToken.getValue())
                        .content(om.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        // then
        result.andExpect(status().isNoContent())
                .andDo(print());

        then(memberService).should().updateProfile(testMember.id(), request);

        // API Docs
        result.andDo(document("update-profile",
                pathParameters(
                        parameterWithName("member-id").description("유저 식별자")
                ),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                )
        ));
    }

    @Test
    void deleteMember_test() throws Exception {

        // given
        long memberId = 1L;
        doNothing().when(memberService).deleteMember(memberId);

        // when
        var result = mvc.perform(delete("/api/members/{member-id}", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, GrantType.BEARER.getType() + " " + accessToken.getValue()))
                .andExpect(status().isNoContent());

        // then
        result.andExpect(status().isNoContent())
                .andDo(print());

        then(memberService).should().deleteMember(memberId);

        // API Docs
        result.andDo(document("delete-member",
                pathParameters(
                        parameterWithName("member-id").description("유저 식별자")
                ),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                )
        ));
    }

    @Test
    void getMemberPosts_test() throws Exception {

        // given
        long postId = 1L;
        String title = "Spring Bean 주입 방식";
        String content = "Spring Bean 주입 방식에는 3가지 방식이 있습니다..";
        Category category = Category.INFORMATION;
        String author = "test";
        List<String> tags = List.of("java", "spring");
        int postLikeCount = 11;
        long createdAt = System.currentTimeMillis();

        long commentId = 1L;
        String commentContent = "새로운 댓글";
        String commentAuthor = "널널한 개발자";
        long commentCreatedAt = System.currentTimeMillis();
        long commentModifiedAt = System.currentTimeMillis();
        int commentLikeCount = 10;

        List<CommentDetail> commentDetails = new ArrayList<>();
        commentDetails.add(new CommentDetail(commentId, commentContent, commentAuthor, commentCreatedAt, commentModifiedAt, postId, commentLikeCount));

        List<PostDetail> postDetails = new ArrayList<>();
        postDetails.add(new PostDetail(postId, title, content, category, author, commentDetails, tags, postLikeCount, createdAt, createdAt));

        int pageNumber = 0;
        int pageSize = 5;
        Sort sort = Sort.by(
                Sort.Order.desc("createdAt")
        );

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<PostDetail> page;
        page = PageableExecutionUtils.getPage(postDetails, pageable, postDetails::size);
        given(postService.getListsByMemberId(eq(testMember.id()), any(Pageable.class)))
                .willReturn(page);

        // when
        var result = mvc.perform(get("/api/members/{member-id}/posts?page={page}&size={size}&sort={property,direction}", testMember.id(), pageNumber, pageSize, "createdAt,desc")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(postId))
                .andExpect(jsonPath("$.content[0].title").value(title))
                .andExpect(jsonPath("$.content[0].contents").value(content))
                .andExpect(jsonPath("$.content[0].category").value(category.name()))
                .andExpect(jsonPath("$.content[0].author").value(author))
                .andExpect(jsonPath("$.content[0].comment_details[0].id").value(commentId))
                .andExpect(jsonPath("$.content[0].comment_details[0].contents").value(commentContent))
                .andExpect(jsonPath("$.content[0].comment_details[0].author").value(commentAuthor))
                .andExpect(jsonPath("$.content[0].comment_details[0].created_at").value(commentCreatedAt))
                .andExpect(jsonPath("$.content[0].comment_details[0].modified_at").value(commentModifiedAt))
                .andExpect(jsonPath("$.content[0].comment_details[0].like_count").value(commentLikeCount))
                .andExpect(jsonPath("$.content[0].comment_details[0].post_id").value(postId))
                .andExpect(jsonPath("$.content[0].tags[0]").value(tags.get(0)))
                .andExpect(jsonPath("$.content[0].tags[1]").value(tags.get(1)))
                .andExpect(jsonPath("$.content[0].like_count").value(postLikeCount))
                .andExpect(jsonPath("$.content[0].created_at").value(createdAt))
                .andExpect(jsonPath("$.content[0].modified_at").value(createdAt))
                .andExpect(jsonPath("$.pageable.offset").value(page.getPageable().getOffset()))
                .andExpect(jsonPath("$.pageable.page_size").value(page.getPageable().getPageSize()))
                .andExpect(jsonPath("$.pageable.paged").value(true))
                .andExpect(jsonPath("$.pageable.unpaged").value(false))
                .andExpect(jsonPath("$.pageable.page_number").value(page.getPageable().getPageNumber()))
                .andExpect(jsonPath("$.pageable.sort").isMap())
                .andExpect(jsonPath("$.pageable.sort.empty").value(false))
                .andExpect(jsonPath("$.pageable.sort.sorted").value(true))
                .andExpect(jsonPath("$.pageable.sort.unsorted").value(false))
                .andExpect(jsonPath("$.total_pages").value(page.getTotalPages()))
                .andExpect(jsonPath("$.total_elements").value(page.getTotalElements()))
                .andExpect(jsonPath("$.last").value(page.isLast()))
                .andExpect(jsonPath("$.first").value(page.isFirst()))
                .andExpect(jsonPath("$.number").value(page.getNumber()))
                .andExpect(jsonPath("$.size").value(page.getSize()))
                .andExpect(jsonPath("$.number_of_elements").value(page.getNumberOfElements()))
                .andExpect(jsonPath("$.empty").value(page.isEmpty()))
                .andExpect(jsonPath("$.first").value(page.isFirst()))
                .andExpect(jsonPath("$.sort").isMap())
                .andDo(print());

        then(postService).should().getListsByMemberId(testMember.id(), pageable);

        // API Docs
        result.andDo(document("get-member-posts",
                pathParameters(
                        parameterWithName("member-id").description("유저 식별자")
                ),
                queryParameters(
                        parameterWithName("page").description("페이지 번호"),
                        parameterWithName("size").description("페이지 사이즈"),
                        parameterWithName("sort").description("정렬 방식")
                ),
                responseFields(
                        fieldWithPath("content").description("게시글 목록"),
                        fieldWithPath("content[].id").description("식별자"),
                        fieldWithPath("content[].title").description("제목"),
                        fieldWithPath("content[].contents").description("내용"),
                        fieldWithPath("content[].category").description("카테고리"),
                        fieldWithPath("content[].author").description("작성자"),
                        fieldWithPath("content[].comment_details").description("댓글 목록"),
                        fieldWithPath("content[].comment_details[].id").description("식별자"),
                        fieldWithPath("content[].comment_details[].contents").description("내용"),
                        fieldWithPath("content[].comment_details[].author").description("작성자"),
                        fieldWithPath("content[].comment_details[].created_at").description("생성일"),
                        fieldWithPath("content[].comment_details[].modified_at").description("수정일"),
                        fieldWithPath("content[].comment_details[].like_count").description("좋아요 수"),
                        fieldWithPath("content[].comment_details[].post_id").description("게시글 식별자"),
                        fieldWithPath("content[].tags").description("태그 목록"),
                        fieldWithPath("content[].like_count").description("좋아요 수"),
                        fieldWithPath("content[].created_at").description("생성일"),
                        fieldWithPath("content[].modified_at").description("수정일"),
                        fieldWithPath("pageable").description("페이지 정보"),
                        fieldWithPath("pageable.offset").description("페이지 오프셋"),
                        fieldWithPath("pageable.page_size").description("페이지 사이즈"),
                        fieldWithPath("pageable.paged").description("페이징 여부"),
                        fieldWithPath("pageable.unpaged").description("페이징 여부"),
                        fieldWithPath("pageable.page_number").description("페이지 번호"),
                        fieldWithPath("pageable.sort").description("정렬 정보"),
                        fieldWithPath("pageable.sort.empty").description("비어있는지 여부"),
                        fieldWithPath("pageable.sort.sorted").description("정렬 여부"),
                        fieldWithPath("pageable.sort.unsorted").description("정렬 여부"),
                        fieldWithPath("total_pages").description("총 페이지 수"),
                        fieldWithPath("total_elements").description("총 게시글 수"),
                        fieldWithPath("last").description("마지막 페이지 여부"),
                        fieldWithPath("first").description("첫번째 페이지 여부"),
                        fieldWithPath("number").description("현재 페이지 번호"),
                        fieldWithPath("size").description("페이지 사이즈"),
                        fieldWithPath("number_of_elements").description("전체 게시글 수"),
                        fieldWithPath("empty").description("비어있는지 여부"),
                        fieldWithPath("sort").description("정렬 정보"),
                        fieldWithPath("sort.empty").description("비어있는지 여부"),
                        fieldWithPath("sort.sorted").description("정렬 여부"),
                        fieldWithPath("sort.unsorted").description("정렬 여부")
                )));
    }

    @Test
    void getMemberComments_test() throws Exception {

        // given
        long postId = 1L;
        long commentId = 1L;
        String commentContent = "새로운 댓글";
        String commentAuthor = "널널한 개발자";
        long commentCreatedAt = System.currentTimeMillis();
        long commentModifiedAt = System.currentTimeMillis();
        int commentLikeCount = 10;

        List<CommentDetail> commentDetails = new ArrayList<>();
        commentDetails.add(new CommentDetail(commentId, commentContent, commentAuthor, commentCreatedAt, commentModifiedAt, postId, commentLikeCount));

        int pageNumber = 0;
        int pageSize = 5;
        Sort sort = Sort.by(
                Sort.Order.desc("createdAt")
        );

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<CommentDetail> page;
        page = PageableExecutionUtils.getPage(commentDetails, pageable, commentDetails::size);
        given(commentService.getListsByMemberId(eq(testMember.id()), any(Pageable.class)))
                .willReturn(page);

        // when
        var result = mvc.perform(get("/api/members/{member-id}/comments?page={page}&size={size}&sort={property,direction}", testMember.id(), pageNumber, pageSize, "createdAt,desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(commentId))
                .andExpect(jsonPath("$.content[0].contents").value(commentContent))
                .andExpect(jsonPath("$.content[0].author").value(commentAuthor))
                .andExpect(jsonPath("$.content[0].created_at").value(commentCreatedAt))
                .andExpect(jsonPath("$.content[0].modified_at").value(commentModifiedAt))
                .andExpect(jsonPath("$.content[0].like_count").value(commentLikeCount))
                .andExpect(jsonPath("$.content[0].post_id").value(postId))
                .andExpect(jsonPath("$.pageable.offset").value(page.getPageable().getOffset()))
                .andExpect(jsonPath("$.pageable.page_size").value(page.getPageable().getPageSize()))
                .andExpect(jsonPath("$.pageable.paged").value(true))
                .andExpect(jsonPath("$.pageable.unpaged").value(false))
                .andExpect(jsonPath("$.pageable.page_number").value(page.getPageable().getPageNumber()))
                .andExpect(jsonPath("$.pageable.sort").isMap())
                .andExpect(jsonPath("$.pageable.sort.empty").value(false))
                .andExpect(jsonPath("$.pageable.sort.sorted").value(true))
                .andExpect(jsonPath("$.pageable.sort.unsorted").value(false))
                .andExpect(jsonPath("$.total_pages").value(page.getTotalPages()))
                .andExpect(jsonPath("$.total_elements").value(page.getTotalElements()))
                .andExpect(jsonPath("$.last").value(page.isLast()))
                .andExpect(jsonPath("$.first").value(page.isFirst()))
                .andExpect(jsonPath("$.number").value(page.getNumber()))
                .andExpect(jsonPath("$.size").value(page.getSize()))
                .andExpect(jsonPath("$.number_of_elements").value(page.getNumberOfElements()))
                .andExpect(jsonPath("$.empty").value(page.isEmpty()))
                .andExpect(jsonPath("$.first").value(page.isFirst()))
                .andExpect(jsonPath("$.sort").isMap())
                .andDo(print());

        then(commentService).should().getListsByMemberId(testMember.id(), pageable);

        // API Docs
        result.andDo(document("get-member-comments",
                pathParameters(
                        parameterWithName("member-id").description("유저 식별자")
                ),
                queryParameters(
                        parameterWithName("page").description("페이지 번호"),
                        parameterWithName("size").description("페이지 사이즈"),
                        parameterWithName("sort").description("정렬 방식")
                ),
                responseFields(
                        fieldWithPath("content").description("댓글 목록"),
                        fieldWithPath("content[].id").description("식별자"),
                        fieldWithPath("content[].contents").description("내용"),
                        fieldWithPath("content[].author").description("작성자"),
                        fieldWithPath("content[].created_at").description("생성일"),
                        fieldWithPath("content[].modified_at").description("수정일"),
                        fieldWithPath("content[].like_count").description("좋아요 수"),
                        fieldWithPath("content[].post_id").description("게시글 식별자"),
                        fieldWithPath("pageable").description("페이지 정보"),
                        fieldWithPath("pageable.offset").description("페이지 오프셋"),
                        fieldWithPath("pageable.page_size").description("페이지 사이즈"),
                        fieldWithPath("pageable.paged").description("페이징 여부"),
                        fieldWithPath("pageable.unpaged").description("페이징 여부"),
                        fieldWithPath("pageable.page_number").description("페이지 번호"),
                        fieldWithPath("pageable.sort").description("정렬 정보"),
                        fieldWithPath("pageable.sort.empty").description("비어있는지 여부"),
                        fieldWithPath("pageable.sort.sorted").description("정렬 여부"),
                        fieldWithPath("pageable.sort.unsorted").description("정렬 여부"),
                        fieldWithPath("total_pages").description("총 페이지 수"),
                        fieldWithPath("total_elements").description("총 게시글 수"),
                        fieldWithPath("last").description("마지막 페이지 여부"),
                        fieldWithPath("first").description("첫번째 페이지 여부"),
                        fieldWithPath("number").description("현재 페이지 번호"),
                        fieldWithPath("size").description("페이지 사이즈"),
                        fieldWithPath("number_of_elements").description("전체 게시글 수"),
                        fieldWithPath("empty").description("비어있는지 여부"),
                        fieldWithPath("sort").description("정렬 정보"),
                        fieldWithPath("sort.empty").description("비어있는지 여부"),
                        fieldWithPath("sort.sorted").description("정렬 여부"),
                        fieldWithPath("sort.unsorted").description("정렬 여부"))));
    }

    @Test
    void emailVerify_test() throws Exception {

        // given
        EmailVerify request = new EmailVerify("test@gmail.com");
        doNothing().when(memberService).emailVerify(eq(request), any(HttpSession.class));

        // when
        var result = mvc.perform(post("/api/members/{member-id}/email/verify", testMember.id())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, GrantType.BEARER.getType() + " " + accessToken.getValue())
                .content(om.writeValueAsString(request)));

        // then
        result.andExpect(status().isNoContent())
                .andDo(print());

        then(memberService).should().emailVerify(eq(request), any(HttpSession.class));

        // API Docs
        result.andDo(document("email-verify",
                pathParameters(
                        parameterWithName("member-id").description("유저 식별자")
                ),
                requestFields(
                        fieldWithPath("email").description("인증을 위한 이메일")
                )));
    }

    @Test
    void emailConfirm_test() throws Exception {

        // given
        EmailConfirm request = new EmailConfirm("test@gmail.com", "123456");
        doNothing().when(memberService).emailConfirm(any(LoginInfo.class), eq(request), any(HttpSession.class));

        // when
        var result = mvc.perform(post("/api/members/{member-id}/email/confirm", testMember.id())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, GrantType.BEARER.getType() + " " + accessToken.getValue())
                .content(om.writeValueAsString(request)));

        // then
        result.andExpect(status().isNoContent())
                .andDo(print());

        then(memberService).should().emailConfirm(any(LoginInfo.class), eq(request), any(HttpSession.class));

        // API Docs
        result.andDo(document("email-confirm",
                pathParameters(
                        parameterWithName("member-id").description("유저 식별자")
                ),
                requestFields(
                        fieldWithPath("email").description("인증을 위한 이메일"),
                        fieldWithPath("code").description("인증 코드")
                )));
    }

}
