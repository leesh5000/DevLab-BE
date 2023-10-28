package com.leesh.devlab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leesh.devlab.constant.Category;
import com.leesh.devlab.constant.Role;
import com.leesh.devlab.constant.TokenType;
import com.leesh.devlab.constant.dto.*;
import com.leesh.devlab.jwt.Token;
import com.leesh.devlab.jwt.TokenService;
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
    void getPost() throws Exception {

        // given
        long postId = 1L;
        String title = "Spring Bean 주입 방식";
        String content = "Spring Bean 주입 방식에는 3가지 방식이 있습니다..";
        Category category = Category.INFORMATION;
        AuthorDto author = new AuthorDto(10L, "개발맨");
        List<String> tags = List.of("java", "spring");
        int postLikeCount = 11;
        int commentCount = 10;
        long createdAt = System.currentTimeMillis();

        PostDto postDto = new PostDto(postId, title, content, category, author, tags, postLikeCount, commentCount, createdAt, createdAt);

        given(postService.getPost(postId))
                .willReturn(postDto);

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
                .andExpect(jsonPath("$.author").exists())
                .andExpect(jsonPath("$.author.nickname").value(author.nickname()))
                .andExpect(jsonPath("$.author.id").value(author.id()))
                .andExpect(jsonPath("$.tags[0]").value(tags.get(0)))
                .andExpect(jsonPath("$.tags[1]").value(tags.get(1)))
                .andExpect(jsonPath("$.like_count").value(postLikeCount))
                .andExpect(jsonPath("$.comment_count").value(commentCount))
                .andExpect(jsonPath("$.created_at").value(createdAt))
                .andExpect(jsonPath("$.modified_at").value(createdAt))
                .andDo(print());

        then(postService).should().getPost(1L);

        // API Docs
        result.andDo(document("posts/getPost",
                pathParameters(
                        parameterWithName("post-id").description("게시글 식별자")
                ),
                responseFields(
                        fieldWithPath("id").description("식별자"),
                        fieldWithPath("title").description("제목"),
                        fieldWithPath("contents").description("내용"),
                        fieldWithPath("category").description("카테고리"),
                        fieldWithPath("author").description("작성자 정보"),
                        fieldWithPath("author.id").description("작성자 식별자"),
                        fieldWithPath("author.nickname").description("작성자 닉네임"),
                        fieldWithPath("tags").description("태그 목록"),
                        fieldWithPath("like_count").description("좋아요 수"),
                        fieldWithPath("comment_count").description("댓글 수"),
                        fieldWithPath("created_at").description("생성일"),
                        fieldWithPath("modified_at").description("수정일")
                )));
    }

    @Test
    void getLists() throws Exception {

        // given
        long postId = 1L;
        String title = "Spring Bean 주입 방식";
        String contents = "Spring Bean 주입 방식에는 3가지 방식이 있습니다..";
        Category category = Category.INFORMATION;
        long createdAt = System.currentTimeMillis();
        long commentCount = 10;
        long likeCount = 10;
        List<String> tags = List.of("spring", "java");

        AuthorDto author = new AuthorDto(10L, "개발맨");

        PostDto postDto = new PostDto(postId, title, contents, category, author, tags, likeCount, commentCount, createdAt, createdAt);
        List<PostDto> content = new ArrayList<>();
        content.add(postDto);

        int pageNumber = 0;
        int pageSize = 5;
        Sort sort = Sort.by(
                Sort.Order.desc("createdAt")
        );
        String keyword = "객체지향의 4가지 원칙";

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<PostDto> posts = PageableExecutionUtils.getPage(content, pageable, content::size);
        given(postService.getPosts(any(Category.class), any(Pageable.class), any(String.class)))
                .willReturn(posts);

        // when
        var result = mvc.perform(get("/api/posts?category={category}&page={page}&size={size}&sort={property,direction}&keyword={keyword}", category, pageNumber, pageSize, "createdAt,desc", keyword)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(postId))
                .andExpect(jsonPath("$.content[0].title").value(title))
                .andExpect(jsonPath("$.content[0].contents").value(contents))
                .andExpect(jsonPath("$.content[0].category").value(category.name()))
                .andExpect(jsonPath("$.content[0].author").exists())
                .andExpect(jsonPath("$.content[0].author.nickname").value(author.nickname()))
                .andExpect(jsonPath("$.content[0].author.id").value(author.id()))
                .andExpect(jsonPath("$.content[0].tags[0]").value(tags.get(0)))
                .andExpect(jsonPath("$.content[0].tags[1]").value(tags.get(1)))
                .andExpect(jsonPath("$.content[0].like_count").value(likeCount))
                .andExpect(jsonPath("$.content[0].comment_count").value(commentCount))
                .andExpect(jsonPath("$.content[0].created_at").value(createdAt))
                .andExpect(jsonPath("$.content[0].modified_at").value(createdAt))
                .andExpect(jsonPath("$.pageable.offset").value(posts.getPageable().getOffset()))
                .andExpect(jsonPath("$.pageable.page_size").value(posts.getPageable().getPageSize()))
                .andExpect(jsonPath("$.pageable.paged").value(true))
                .andExpect(jsonPath("$.pageable.unpaged").value(false))
                .andExpect(jsonPath("$.pageable.page_number").value(posts.getPageable().getPageNumber()))
                .andExpect(jsonPath("$.pageable.sort").isMap())
                .andExpect(jsonPath("$.pageable.sort.empty").value(false))
                .andExpect(jsonPath("$.pageable.sort.sorted").value(true))
                .andExpect(jsonPath("$.pageable.sort.unsorted").value(false))
                .andExpect(jsonPath("$.total_pages").value(posts.getTotalPages()))
                .andExpect(jsonPath("$.total_elements").value(posts.getTotalElements()))
                .andExpect(jsonPath("$.last").value(posts.isLast()))
                .andExpect(jsonPath("$.first").value(posts.isFirst()))
                .andExpect(jsonPath("$.number").value(posts.getNumber()))
                .andExpect(jsonPath("$.size").value(posts.getSize()))
                .andExpect(jsonPath("$.number_of_elements").value(posts.getNumberOfElements()))
                .andExpect(jsonPath("$.empty").value(posts.isEmpty()))
                .andExpect(jsonPath("$.first").value(posts.isFirst()))
                .andExpect(jsonPath("$.sort").isMap())
                .andDo(print());

        then(postService).should().getPosts(any(Category.class), any(Pageable.class), any(String.class));

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
                        fieldWithPath("content[].author.nickname").description("작성자 닉네임"),
                        fieldWithPath("content[].author.id").description("작성자 식별자"),
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
        CreatePostRequestDto request = new CreatePostRequestDto("test title", "test contents", Category.QUESTION, Set.of("java", "spring"));
        CreatePostResponseDto response = new CreatePostResponseDto(newPostId);

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

        then(postService).should().create(any(CreatePostRequestDto.class), any(LoginMemberDto.class));

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
        CreatePostRequestDto requestDto = new CreatePostRequestDto("test title", "test contents", Category.QUESTION, Set.of("java", "spring"));

        doNothing().when(postService).put(postId, requestDto, testMember);

        // when
        var result = mvc.perform(RestDocumentationRequestBuilders.put("/api/posts/{post-id}", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken.getValue())
                .content(om.writeValueAsString(requestDto)));

        // then
        result.andExpect(status().isNoContent())
                .andDo(print());

        then(postService).should().put(postId, requestDto, testMember);

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
        CreateCommentRequestDto requestDto = new CreateCommentRequestDto("새로운 댓글");
        CreateCommentResponseDto responseDto = new CreateCommentResponseDto(commentId);

        given(commentService.create(requestDto, testMember, postId))
                .willReturn(responseDto);

        // when
        var result = mvc.perform(post("/api/posts/{post-id}/comments", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken.getValue())
                .content(om.writeValueAsString(requestDto)));

        // then
        result

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.comment_id").value(commentId))
                .andDo(print());

        then(commentService).should().create(any(CreateCommentRequestDto.class), any(LoginMemberDto.class), any(Long.class));

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
        CreateCommentRequestDto requestDto = new CreateCommentRequestDto("새로운 댓글");
        CreateCommentResponseDto responseDto = new CreateCommentResponseDto(commentId);

        doNothing().when(commentService).put(requestDto, testMember, postId, commentId);

        // when
        var result = mvc.perform(RestDocumentationRequestBuilders.put("/api/posts/{post-id}/comments/{comment-id}", postId, commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken.getValue())
                .content(om.writeValueAsString(requestDto)));

        // then
        result.andExpect(status().isNoContent())
                .andDo(print());

        then(commentService).should().put(requestDto, testMember, postId, commentId);

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
        LikeResponseDto response = new LikeResponseDto(likeId, now, now);
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