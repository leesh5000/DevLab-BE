package com.leesh.devlab.service;

import com.leesh.devlab.constant.dto.*;
import com.leesh.devlab.domain.comment.CommentRepository;
import com.leesh.devlab.domain.hashtag.Hashtag;
import com.leesh.devlab.domain.hashtag.HashtagRepository;
import com.leesh.devlab.domain.like.LikeRepository;
import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.domain.member.MemberRepository;
import com.leesh.devlab.constant.Category;
import com.leesh.devlab.domain.post.Post;
import com.leesh.devlab.domain.post.PostRepository;
import com.leesh.devlab.domain.tag.Tag;
import com.leesh.devlab.constant.ErrorCode;
import com.leesh.devlab.exception.custom.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final TagService tagService;
    private final HashtagRepository hashtagRepository;
    private final LikeRepository likeRepository;
    private final CommentService commentService;
    private final MemberService memberService;

    @Transactional
    public CreatePostResponseDto create(CreatePostRequestDto requestDto, LoginMemberDto loginMemberDto) {

        Member member = memberRepository.getReferenceById(loginMemberDto.id());

        // 게시글을 생성한다.
        Post post = requestDto.toEntity(member);

        // DB에서 태그 목록을 조회한다.
        List<Tag> tags = tagService.getAllByNames(requestDto.tagNames());

        // 게시글에 해시태그를 추가한다.
        post.tagging(tags);

        Long postId = postRepository.save(post).getId();

        return new CreatePostResponseDto(postId);
    }

    @Transactional
    public void put(Long postId, CreatePostRequestDto requestDto, LoginMemberDto loginMemberDto) {

        Optional<Post> optional = postRepository.findById(postId);

        if (optional.isPresent()) {
            edit(requestDto, loginMemberDto, optional.get());
        } else {
            create(requestDto, loginMemberDto);
        }
    }

    private void edit(CreatePostRequestDto requestDto, LoginMemberDto loginMemberDto, Post post) {

        validateAuthor(loginMemberDto, post);

        // 유저가 새로 입력한 태그 목록을 조회한다.
        List<Tag> newTags = tagService.getAllByNames(requestDto.tagNames());

        // 게시글을 수정한다.
        post.edit(requestDto.title(), requestDto.contents(), requestDto.category(), newTags);
    }

    private Post getByIdWithMember(Long postId) {
        return postRepository.findByIdWithMember(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_RESOURCE, "not found"));
    }

    @Transactional
    public void delete(Long postId, LoginMemberDto loginMemberDto) {

        Post findPost = getByIdWithMember(postId);

        validateAuthor(loginMemberDto, findPost);

        postRepository.delete(findPost);
    }

    public static void validateAuthor(LoginMemberDto loginMemberDto, Post post) {
        // 게시글의 작성자인지 검증
        if (!Objects.equals(loginMemberDto.id(), post.getMember().getId())) {
            throw new BusinessException(ErrorCode.NOT_RESOURCE_OWNER, "not post author");
        }
    }

    @Transactional(readOnly = true)
    public PostDetailDto getDetail(Long postId) {

        Post post = getByIdWithMember(postId);

        return generatePostDetail(post);
    }

    private PostDetailDto generatePostDetail(Post post) {

        List<String> tags = post.getHashtags().stream()
                .map(Hashtag::getTag)
                .map(Tag::getName)
                .toList();

        int likeCount = post.getLikes().size();

        List<CommentDetailDto> commentDetailDtos = post.getComments().stream()
                .map(commentService::generateCommentDetail)
                .toList();

        return PostDetailDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .contents(post.getContents())
                .category(post.getCategory())
                .author(post.getMember().getNickname())
                .tags(tags)
                .likeCount(likeCount)
                .commentDetailDtos(commentDetailDtos)
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public Page<PostInfoDto> getLists(Category category, Pageable pageable, String keyword) {
        return postRepository.getPostPage(category, pageable, keyword, null);
    }

    @Transactional(readOnly = true)
    public Page<PostInfoDto> getLists(Pageable pageable, Long memberId) {
        return postRepository.getPostPage(null, pageable, null, memberId);
    }

    @Transactional(readOnly = true)
    public Page<PostDetailDto> getListsByMemberId(Long memberId, Pageable pageable) {

        memberService.existById(memberId);

        Page<Post> page = postRepository.findAllWithMemberByMemberId(memberId, pageable);

        return page.map(this::generatePostDetail);
    }
}
