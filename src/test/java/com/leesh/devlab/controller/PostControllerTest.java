package com.leesh.devlab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leesh.devlab.domain.member.Role;
import com.leesh.devlab.domain.post.Category;
import com.leesh.devlab.dto.*;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@Import(WebMvcTestConfig.class)
@AutoConfigureRestDocs
class PostControllerTest {

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

        accessToken = new Jwt(TokenType.ACCESS, "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBQ0NFU1MiLCJpYXQiOjE2NzUyMTA4NzksImV4cCI6MTY3NTIxMTc3OSwidXNlcklkIjoxLCJyb2xlIjoiVVNFUiJ9.X1AfxGWGUPhC5ovt3hcLv8_6Zb8H0Z4yn8tDxHohrTx_kcgTDWIHPt8yDuTHYo9KmqqqIwTQ7VEtMaVyJdqKrQ", TokenType.ACCESS.getExpiresInSeconds());
        testMember = new LoginInfo(1L, "test", Role.MEMBER);

        doNothing().when(tokenService).validateToken(accessToken.getValue(), accessToken.getTokenType());
        given(tokenService.extractLoginInfo(accessToken.getValue()))
                .willReturn(testMember);

    }

    @Test
    void getDetail() throws Exception {

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
        PostDetail postDetail = new PostDetail(postId, title, content, category, author, commentDetails, tags, postLikeCount, createdAt, createdAt);

        given(postService.getDetail(postId))
                .willReturn(postDetail);

        // when
        var result = mvc.perform(get("/api/posts/{post-id}", postId)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postId))
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.contents").value(content))
                .andExpect(jsonPath("$.category").value(category.name()))
                .andExpect(jsonPath("$.author").value(author))
                .andExpect(jsonPath("$.comment_details[0].id").value(commentId))
                .andExpect(jsonPath("$.comment_details[0].contents").value(commentContent))
                .andExpect(jsonPath("$.comment_details[0].author").value(commentAuthor))
                .andExpect(jsonPath("$.comment_details[0].created_at").value(commentCreatedAt))
                .andExpect(jsonPath("$.comment_details[0].modified_at").value(commentModifiedAt))
                .andExpect(jsonPath("$.comment_details[0].like_count").value(commentLikeCount))
                .andExpect(jsonPath("$.comment_details[0].post_id").value(postId))
                .andExpect(jsonPath("$.tags[0]").value(tags.get(0)))
                .andExpect(jsonPath("$.tags[1]").value(tags.get(1)))
                .andExpect(jsonPath("$.like_count").value(postLikeCount))
                .andExpect(jsonPath("$.created_at").value(createdAt))
                .andExpect(jsonPath("$.modified_at").value(createdAt))
                .andDo(print());

        then(postService).should().getDetail(1L);

        // API Docs
        result.andDo(document("posts/get-detail",
                pathParameters(
                        parameterWithName("post-id").description("게시글 식별자")
                ),
                responseFields(
                        fieldWithPath("id").description("식별자"),
                        fieldWithPath("title").description("제목"),
                        fieldWithPath("contents").description("내용"),
                        fieldWithPath("category").description("카테고리"),
                        fieldWithPath("author").description("작성자"),
                        fieldWithPath("comment_details").description("댓글 목록"),
                        fieldWithPath("comment_details[].id").description("식별자"),
                        fieldWithPath("comment_details[].contents").description("내용"),
                        fieldWithPath("comment_details[].author").description("작성자"),
                        fieldWithPath("comment_details[].created_at").description("생성일"),
                        fieldWithPath("comment_details[].modified_at").description("수정일"),
                        fieldWithPath("comment_details[].like_count").description("좋아요 수"),
                        fieldWithPath("comment_details[].post_id").description("게시글 식별자"),
                        fieldWithPath("tags").description("태그 목록"),
                        fieldWithPath("like_count").description("좋아요 수"),
                        fieldWithPath("created_at").description("생성일"),
                        fieldWithPath("modified_at").description("수정일")
                )));
    }

    @Test
    void getLists() throws Exception {

        // given
        long postId = 1L;
        String title = "Spring Bean 주입 방식";
        String content = "Spring Bean 주입 방식에는 3가지 방식이 있습니다..";
        Category category = Category.INFORMATION;
        String author = "test";
        long createdAt = System.currentTimeMillis();
        long commentCount = 10;
        long likeCount = 10;
        List<String> tags = List.of("spring", "java");

        PostInfo postInfo = new PostInfo(postId, title, content, category, createdAt, createdAt, author, commentCount, likeCount, tags);
        List<PostInfo> postInfos = new ArrayList<>();
        postInfos.add(postInfo);

        int pageNumber = 0;
        int pageSize = 5;
        Sort sort = Sort.by(
                Sort.Order.desc("createdAt")
        );
        String keyword = "객체지향의 4가지 원칙";

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<PostInfo> page;
        page = PageableExecutionUtils.getPage(postInfos, pageable, postInfos::size);
        given(postService.getLists(any(Category.class), any(Pageable.class), any(String.class)))
                .willReturn(page);

        // when
        var result = mvc.perform(get("/api/posts?category={category}&page={page}&size={size}&sort={property,direction}&keyword={keyword}", category, pageNumber, pageSize, "createdAt,desc", keyword)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(postId))
                .andExpect(jsonPath("$.content[0].title").value(title))
                .andExpect(jsonPath("$.content[0].contents").value(content))
                .andExpect(jsonPath("$.content[0].category").value(category.name()))
                .andExpect(jsonPath("$.content[0].author").value(author))
                .andExpect(jsonPath("$.content[0].tags[0]").value(tags.get(0)))
                .andExpect(jsonPath("$.content[0].tags[1]").value(tags.get(1)))
                .andExpect(jsonPath("$.content[0].like_count").value(likeCount))
                .andExpect(jsonPath("$.content[0].comment_count").value(commentCount))
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

        then(postService).should().getLists(any(Category.class), any(Pageable.class), any(String.class));

        // API Docs
        result.andDo(document("posts/get-lists",
                queryParameters(
                        parameterWithName("category").description("카테고리"),
                        parameterWithName("page").description("페이지 번호"),
                        parameterWithName("size").description("페이지 사이즈"),
                        parameterWithName("sort").description("정렬 방식"),
                        parameterWithName("keyword").description("검색어")
                ),
                responseFields(
                        fieldWithPath("content").description("게시글 목록"),
                        fieldWithPath("content[].id").description("식별자"),
                        fieldWithPath("content[].title").description("제목"),
                        fieldWithPath("content[].contents").description("내용"),
                        fieldWithPath("content[].category").description("카테고리"),
                        fieldWithPath("content[].author").description("작성자"),
                        fieldWithPath("content[].tags").description("태그 목록"),
                        fieldWithPath("content[].like_count").description("좋아요 수"),
                        fieldWithPath("content[].comment_count").description("댓글 수"),
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
    void create() throws Exception {

        // given
        long newPostId = 1L;
        CreatePost.Request request = new CreatePost.Request("test title", "test contents", Category.QUESTION, Set.of("java", "spring"));
        CreatePost.Response response = new CreatePost.Response(newPostId);

        given(postService.create(request, testMember))
                .willReturn(response);

        // when
        var result = mvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken.getValue())
                .content(om.writeValueAsString(request)));

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.post_id").value(newPostId))
                .andDo(print());

        then(postService).should().create(any(CreatePost.Request.class), any(LoginInfo.class));

        // API Docs
        result.andDo(document("posts/create",
                requestHeaders(
                        headerWithName("Authorization").description("액세스 토큰")
                ),
                requestFields(
                        fieldWithPath("title").description("제목"),
                        fieldWithPath("contents").description("내용"),
                        fieldWithPath("category").description("카테고리"),
                        fieldWithPath("tags").description("태그 목록")
                ),
                responseFields(
                        fieldWithPath("post_id").description("생성된 게시글 식별자")
                )));
    }

    @Test
    void put() throws Exception {

        // given
        long postId = 1L;
        CreatePost.Request request = new CreatePost.Request("test title", "test contents", Category.QUESTION, Set.of("java", "spring"));

        doNothing().when(postService).put(postId, request, testMember);

        // when
        var result = mvc.perform(RestDocumentationRequestBuilders.put("/api/posts/{post-id}", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken.getValue())
                .content(om.writeValueAsString(request)));

        // then
        result.andExpect(status().isNoContent())
                .andDo(print());

        then(postService).should().put(postId, request, testMember);

        // API Docs
        result.andDo(document("posts/put",
                requestHeaders(
                        headerWithName("Authorization").description("액세스 토큰")
                ),
                pathParameters(
                        parameterWithName("post-id").description("게시글 식별자")
                ),
                requestFields(
                        fieldWithPath("title").description("제목"),
                        fieldWithPath("contents").description("내용"),
                        fieldWithPath("category").description("카테고리"),
                        fieldWithPath("tags").description("태그 목록")
                )));
    }

    @Test
    void delete() throws Exception {

        // given
        long postId = 1L;
        doNothing().when(postService).delete(postId, testMember);

        // when
        var result = mvc.perform(RestDocumentationRequestBuilders.delete("/api/posts/{post-id}", postId)
                .header("Authorization", "Bearer " + accessToken.getValue()));

        // then
        result
                .andExpect(status().isNoContent())
                .andDo(print());

        then(postService).should().delete(postId, testMember);

        // API Docs
        result.andDo(document("posts/delete",
                requestHeaders(
                        headerWithName("Authorization").description("액세스 토큰")
                ),
                pathParameters(
                        parameterWithName("post-id").description("게시글 식별자")
                )));
    }

    @Test
    void createComment() throws Exception {

        // given
        long postId = 1L;
        long commentId = 1L;
        CreateComment.Request request = new CreateComment.Request("새로운 댓글");
        CreateComment.Response response = new CreateComment.Response(commentId);

        given(commentService.create(request, testMember, postId))
                .willReturn(response);

        // when
        var result = mvc.perform(post("/api/posts/{post-id}/comments", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken.getValue())
                .content(om.writeValueAsString(request)));

        // then
        result

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.comment_id").value(commentId))
                .andDo(print());

        then(commentService).should().create(any(CreateComment.Request.class), any(LoginInfo.class), any(Long.class));

        // API Docs
        result.andDo(document("posts/create-comment",
                requestHeaders(
                        headerWithName("Authorization").description("액세스 토큰")
                ),
                pathParameters(
                        parameterWithName("post-id").description("게시글 식별자")
                ),
                requestFields(
                        fieldWithPath("contents").description("댓글 내용")
                ),
                responseFields(
                        fieldWithPath("comment_id").description("생성된 댓글 식별자")
                )));
    }

    @Test
    void putComment() throws Exception {

        // given
        long postId = 1L;
        long commentId = 1L;
        CreateComment.Request request = new CreateComment.Request("새로운 댓글");
        CreateComment.Response response = new CreateComment.Response(commentId);

        doNothing().when(commentService).put(request, testMember, postId, commentId);

        // when
        var result = mvc.perform(RestDocumentationRequestBuilders.put("/api/posts/{post-id}/comments/{comment-id}", postId, commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken.getValue())
                .content(om.writeValueAsString(request)));

        // then
        result.andExpect(status().isNoContent())
                .andDo(print());

        then(commentService).should().put(request, testMember, postId, commentId);

        // API Docs
        result.andDo(document("posts/put-comment",
                requestHeaders(
                        headerWithName("Authorization").description("액세스 토큰")
                ),
                pathParameters(
                        parameterWithName("post-id").description("게시글 식별자"),
                        parameterWithName("comment-id").description("댓글 식별자")
                ),
                requestFields(
                        fieldWithPath("contents").description("댓글 내용")
                )));
    }

    @Test
    void createLike() throws Exception {

        // given
        long postId = 1L;
        long likeId = 1L;
        long now = System.currentTimeMillis();
        LikeInfo response = new LikeInfo(likeId, now, now);
        given(likeService.createPostLike(testMember, postId))
                .willReturn(response);

        // when
        var result = mvc.perform(post("/api/posts/{post-id}/likes", postId)
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

        then(likeService).should().createPostLike(testMember, postId);

        // API Docs
        result.andDo(document("posts/create-like",
                requestHeaders(
                        headerWithName("Authorization").description("액세스 토큰")
                ),
                pathParameters(
                        parameterWithName("post-id").description("게시글 식별자")
                ),
                responseFields(
                        fieldWithPath("like_id").description("좋아요 식별자"),
                        fieldWithPath("created_at").description("생성일"),
                        fieldWithPath("modified_at").description("수정일")
                )));
    }
}