package com.leesh.devlab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leesh.devlab.constant.dto.*;
import com.leesh.devlab.constant.Role;
import com.leesh.devlab.constant.Category;
import com.leesh.devlab.constant.GrantType;
import com.leesh.devlab.jwt.Token;
import com.leesh.devlab.jwt.TokenService;
import com.leesh.devlab.constant.TokenType;
import com.leesh.devlab.jwt.implementation.Jwt;
import com.leesh.devlab.service.CommentService;
import com.leesh.devlab.service.MemberService;
import com.leesh.devlab.service.PostService;
import config.WebMvcTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private LoginMemberDto testMember;

    @BeforeEach
    void setUp() {

        accessToken = new Jwt(TokenType.ACCESS, "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBQ0NFU1MiLCJpYXQiOjE2NzUyMTA4NzksImV4cCI6MTY3NTIxMTc3OSwidXNlcklkIjoxLCJyb2xlIjoiVVNFUiJ9.X1AfxGWGUPhC5ovt3hcLv8_6Zb8H0Z4yn8tDxHohrTx_kcgTDWIHPt8yDuTHYo9KmqqqIwTQ7VEtMaVyJdqKrQ", TokenType.ACCESS.getExpiresInSeconds());
        testMember = new LoginMemberDto(1L, "test", Role.MEMBER);

        doNothing().when(tokenService).validateToken(accessToken.getValue(), accessToken.getTokenType());
        given(tokenService.extractLoginInfo(accessToken.getValue()))
                .willReturn(testMember);

    }

    @Test
    void getMyProfile_test() throws Exception {

        // given
        String loginId = "test";
        String securityCode = UUID.randomUUID().toString();
        String introduce = "안녕하세요 ^^ 즐겁게 개발을 하고있는 개발자입니다.";
        long createdAt = System.currentTimeMillis();
        int postCount = 11;
        int postLikeCount = 112;
        int commentCount = 22;
        int commentLikeCount = 32;

        ActivityDto activities = new ActivityDto(postCount, postLikeCount, commentCount, commentLikeCount);
        MyProfileResponseDto response = new MyProfileResponseDto(testMember.id(), loginId, null, testMember.nickname(), createdAt, securityCode, introduce, activities);

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
                .andExpect(jsonPath("$.oauth").doesNotExist())
                .andExpect(jsonPath("$.nickname").value(testMember.nickname()))
                .andExpect(jsonPath("$.created_at").value(createdAt))
                .andExpect(jsonPath("$.security_code").value(securityCode))
                .andExpect(jsonPath("$.introduce").value(introduce))
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
                        fieldWithPath("oauth").description("소셜 로그인 정보"),
                        fieldWithPath("nickname").description("닉네임"),
                        fieldWithPath("created_at").description("생성일"),
                        fieldWithPath("security_code").description("보안 코드"),
                        fieldWithPath("introduce").description("내 소개"),
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
        ActivityDto activities = new ActivityDto(10, 1, 32, 13);
        String introduce = "안녕하세요 ^^ 개발을 즐기는 ...";
        MemberProfileRequestDto response = new MemberProfileRequestDto(testMember.nickname(), System.currentTimeMillis(), introduce, activities);

        given(memberService.getMemberProfile(testMember.id()))
                .willReturn(response);

        // when
        var result = mvc.perform(get("/api/members/{member-id}", testMember.id())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, GrantType.BEARER.getType() + " " + accessToken.getValue()));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value(testMember.nickname()))
                .andExpect(jsonPath("$.created_at").value(response.createdAt()))
                .andExpect(jsonPath("$.introduce").value(introduce))
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
                        fieldWithPath("nickname").description("닉네임"),
                        fieldWithPath("created_at").description("생성일"),
                        fieldWithPath("introduce").description("내 소개"),
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
        EmailDto emailDto = new EmailDto("name@company.com", true);
        String introduce = "안녕하세요 ^^ 개발을 즐기는 개발자입니다.";
        UpdateProfileRequestDto request = new UpdateProfileRequestDto("newuser", emailDto, introduce);
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
                ),
                requestFields(
                        fieldWithPath("nickname").description("수정할 닉네임"),
                        fieldWithPath("email").description("이메일"),
                        fieldWithPath("email.address").description("보안코드를 발급받는 이메일 주소"),
                        fieldWithPath("email.verified").description("이메일 인증 여부"),
                        fieldWithPath("introduce").description("내 소개")
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
        long postLikeCount = 11;
        long createdAt = System.currentTimeMillis();
        long modifiedAt = System.currentTimeMillis();
        long commentCount = 10;
        int pageNumber = 0;
        int pageSize = 5;
        Sort sort = Sort.by(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        PostInfoDto postInfoDto = new PostInfoDto(postId, title, content, category, createdAt, modifiedAt, author, commentCount, postLikeCount, tags);
        List<PostInfoDto> postInfoDtos = new ArrayList<>();
        postInfoDtos.add(postInfoDto);
        Page<PostInfoDto> postPage = new PageImpl<>(postInfoDtos, pageable, postInfoDtos.size());

        given(postService.getPosts(pageable, testMember.id()))
                .willReturn(postPage);

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
                .andExpect(jsonPath("$.content[0].created_at").value(createdAt))
                .andExpect(jsonPath("$.content[0].modified_at").value(modifiedAt))
                .andExpect(jsonPath("$.content[0].author").value(author))
                .andExpect(jsonPath("$.content[0].comment_count").value(commentCount))
                .andExpect(jsonPath("$.content[0].like_count").value(postLikeCount))
                .andExpect(jsonPath("$.content[0].tags").exists())
                .andExpect(jsonPath("$.content[0].tags[0]").value(tags.get(0)))
                .andExpect(jsonPath("$.content[0].tags[1]").value(tags.get(1)))
                .andExpect(jsonPath("$.pageable.offset").value(postPage.getPageable().getOffset()))
                .andExpect(jsonPath("$.pageable.page_size").value(postPage.getPageable().getPageSize()))
                .andExpect(jsonPath("$.pageable.paged").value(true))
                .andExpect(jsonPath("$.pageable.unpaged").value(false))
                .andExpect(jsonPath("$.pageable.page_number").value(postPage.getPageable().getPageNumber()))
                .andExpect(jsonPath("$.pageable.sort").isMap())
                .andExpect(jsonPath("$.pageable.sort.empty").value(false))
                .andExpect(jsonPath("$.pageable.sort.sorted").value(true))
                .andExpect(jsonPath("$.pageable.sort.unsorted").value(false))
                .andExpect(jsonPath("$.total_pages").value(postPage.getTotalPages()))
                .andExpect(jsonPath("$.total_elements").value(postPage.getTotalElements()))
                .andExpect(jsonPath("$.last").value(postPage.isLast()))
                .andExpect(jsonPath("$.first").value(postPage.isFirst()))
                .andExpect(jsonPath("$.number").value(postPage.getNumber()))
                .andExpect(jsonPath("$.size").value(postPage.getSize()))
                .andExpect(jsonPath("$.number_of_elements").value(postPage.getNumberOfElements()))
                .andExpect(jsonPath("$.empty").value(postPage.isEmpty()))
                .andExpect(jsonPath("$.first").value(postPage.isFirst()))
                .andExpect(jsonPath("$.sort").isMap())
                .andDo(print());

        then(postService).should().getPosts(pageable, testMember.id());

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
                        fieldWithPath("content[].created_at").description("생성일"),
                        fieldWithPath("content[].modified_at").description("수정일"),
                        fieldWithPath("content[].author").description("작성자"),
                        fieldWithPath("content[].comment_count").description("댓글 수"),
                        fieldWithPath("content[].like_count").description("좋아요 수"),
                        fieldWithPath("content[].tags").description("태그 목록"),
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
        int pageNumber = 0;
        int pageSize = 5;
        Sort sort = Sort.by(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        String postTitle = "Spring Bean 주입 방식";
        CommentDto commentDto = new CommentDto(commentId, commentContent, commentAuthor, commentLikeCount, commentCreatedAt, commentModifiedAt, new CommentDto.PostDto(postId, postTitle, Category.INFORMATION));
        List<CommentDto> commentDtos = new ArrayList<>();
        commentDtos.add(commentDto);
        Page<CommentDto> commentPage = new PageImpl<>(commentDtos, pageable, commentDtos.size());

        given(commentService.getLists(pageable, testMember.id()))
                .willReturn(commentPage);

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
                .andExpect(jsonPath("$.content[0].post").exists())
                .andExpect(jsonPath("$.content[0].post.id").value(postId))
                .andExpect(jsonPath("$.content[0].post.title").value(postTitle))
                .andExpect(jsonPath("$.content[0].post.category").value(Category.INFORMATION.name()))
                .andExpect(jsonPath("$.pageable.offset").value(commentPage.getPageable().getOffset()))
                .andExpect(jsonPath("$.pageable.page_size").value(commentPage.getPageable().getPageSize()))
                .andExpect(jsonPath("$.pageable.paged").value(true))
                .andExpect(jsonPath("$.pageable.unpaged").value(false))
                .andExpect(jsonPath("$.pageable.page_number").value(commentPage.getPageable().getPageNumber()))
                .andExpect(jsonPath("$.pageable.sort").isMap())
                .andExpect(jsonPath("$.pageable.sort.empty").value(false))
                .andExpect(jsonPath("$.pageable.sort.sorted").value(true))
                .andExpect(jsonPath("$.pageable.sort.unsorted").value(false))
                .andExpect(jsonPath("$.total_pages").value(commentPage.getTotalPages()))
                .andExpect(jsonPath("$.total_elements").value(commentPage.getTotalElements()))
                .andExpect(jsonPath("$.last").value(commentPage.isLast()))
                .andExpect(jsonPath("$.first").value(commentPage.isFirst()))
                .andExpect(jsonPath("$.number").value(commentPage.getNumber()))
                .andExpect(jsonPath("$.size").value(commentPage.getSize()))
                .andExpect(jsonPath("$.number_of_elements").value(commentPage.getNumberOfElements()))
                .andExpect(jsonPath("$.empty").value(commentPage.isEmpty()))
                .andExpect(jsonPath("$.first").value(commentPage.isFirst()))
                .andExpect(jsonPath("$.sort").isMap())
                .andDo(print());

        then(commentService).should().getLists(pageable, testMember.id());

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
                        fieldWithPath("content[].post").description("해당 댓글이 달린 게시글"),
                        fieldWithPath("content[].post.id").description("게시글 식별자"),
                        fieldWithPath("content[].post.title").description("게시글 제목"),
                        fieldWithPath("content[].post.category").description("게시글 카테고리"),
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

}
