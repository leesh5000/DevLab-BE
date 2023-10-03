package com.leesh.devlab.service;

import com.leesh.devlab.api.post.dto.CreatePost;
import com.leesh.devlab.api.post.dto.PostDetails;
import com.leesh.devlab.domain.hashtag.Hashtag;
import com.leesh.devlab.domain.hashtag.HashtagRepository;
import com.leesh.devlab.domain.like.Like;
import com.leesh.devlab.domain.like.LikeRepository;
import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.domain.member.MemberRepository;
import com.leesh.devlab.domain.post.Post;
import com.leesh.devlab.domain.post.PostRepository;
import com.leesh.devlab.domain.tag.Tag;
import com.leesh.devlab.exception.ErrorCode;
import com.leesh.devlab.exception.custom.BusinessException;
import com.leesh.devlab.jwt.dto.LoginInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class PostService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final TagService tagService;
    private final HashtagRepository hashtagRepository;
    private final LikeRepository likeRepository;

    public CreatePost.Response create(CreatePost.Request requestDto, LoginInfo loginInfo) {

        Member member = memberRepository.getReferenceById(loginInfo.id());

        // 게시글을 생성한다.
        Post post = requestDto.toEntity(member);

        // DB에서 태그 목록을 조회한다.
        List<Tag> tags = tagService.getAllByNames(requestDto.tagNames());

        // 게시글에 해시태그를 추가한다.
        post.tagging(tags);

        Long postId = postRepository.save(post).getId();

        return CreatePost.Response.from(postId);
    }

    public void put(Long postId, CreatePost.Request requestDto, LoginInfo loginInfo) {

        Optional<Post> optional = postRepository.findById(postId);

        if (optional.isPresent()) {
            edit(requestDto, loginInfo, optional);
        } else {
            create(requestDto, loginInfo);
        }
    }

    private void edit(CreatePost.Request requestDto, LoginInfo loginInfo, Optional<Post> optional) {
        Post findPost = optional.get();
        validateAuthor(loginInfo, findPost);

        // 유저가 새로 입력한 태그 목록을 조회한다.
        List<Tag> newTags = tagService.getAllByNames(requestDto.tagNames());

        // 게시글을 수정한다.
        findPost.edit(requestDto.title(), requestDto.contents(), requestDto.category(), newTags);
    }

    private Post getWithHashtagsById(Long postId) {
        return postRepository.findByIdWithHashtags(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_POST, "not found"));
    }

    public void delete(Long postId, LoginInfo loginInfo) {

        Post findPost = getWithHashtagsById(postId);

        validateAuthor(loginInfo, findPost);

        // 자식 데이터들을 먼저 삭제한다.
        deleteAllChildren(findPost);

        postRepository.delete(findPost);

    }

    /**
     * <p>
     *     자식 엔티티들을 모두 삭제하는 메서드 <br>
     *     현재 정책 상, 게시글이 삭제되더라도 댓글은 삭제되지 않는다. <br>
     * </p>
     * @param findPost
     */
    private void deleteAllChildren(Post findPost) {

        List<Long> hashtagIds = findPost.getHashtags()
                .stream()
                .map(Hashtag::getId)
                .toList();

        List<Long> likeIds = findPost.getLikes()
                .stream()
                .map(Like::getId)
                .toList();

        hashtagRepository.deleteAllByIdInBatch(hashtagIds);
        likeRepository.deleteAllByIdInBatch(likeIds);
    }

    public static void validateAuthor(LoginInfo loginInfo, Post post) {
        // 게시글의 작성자인지 검증
        if (!Objects.equals(loginInfo.id(), post.getMember().getId())) {
            throw new BusinessException(ErrorCode.NOT_POST_AUTHOR, "not post author");
        }
    }

    public PostDetails getDetail(Long postId) {

        Post post = getWithHashtagsById(postId);

        List<String> tags = post.getHashtags().stream()
                .map(Hashtag::getTag)
                .map(Tag::getName)
                .toList();

        int likeCount = post.getLikes().size();

        return PostDetails.builder()
                .id(post.getId())
                .title(post.getTitle())
                .contents(post.getContents())
                .category(post.getCategory().name())
                .author(post.getMember().getNickname())
                .tags(tags)
                .likeCount(likeCount)
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .build();
    }

    public List<Post> getByMemberIdWithLikes(Long memberId) {
        return postRepository.findAllByMemberIdWithLikes(memberId);
    }
}
