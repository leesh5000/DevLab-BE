package com.leesh.devlab.service;

import com.leesh.devlab.domain.comment.Comment;
import com.leesh.devlab.domain.comment.CommentRepository;
import com.leesh.devlab.domain.like.Like;
import com.leesh.devlab.domain.like.LikeRepository;
import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.domain.member.MemberRepository;
import com.leesh.devlab.domain.post.Post;
import com.leesh.devlab.domain.post.PostRepository;
import com.leesh.devlab.constant.dto.LikeResponseDto;
import com.leesh.devlab.constant.ErrorCode;
import com.leesh.devlab.exception.custom.BusinessException;
import com.leesh.devlab.constant.dto.LoginMemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public LikeResponseDto createPostLike(LoginMemberDto loginMemberDto, Long postId) {

        Post post = postRepository.getReferenceById(postId);

        post.getLikes().stream()
                .filter(like -> like.getMember().getId().equals(loginMemberDto.id()))
                .findAny()
                .ifPresent((like) -> {
                    throw new BusinessException(ErrorCode.ALREADY_LIKED_POST, "already liked post, member(id) = " + loginMemberDto.id());
                });

        Member member = memberRepository.getReferenceById(loginMemberDto.id());

        Like newLike = Like.builder()
                .post(post)
                .member(member)
                .build();

        likeRepository.saveAndFlush(newLike);

        return LikeResponseDto.from(newLike);
    }

    @Transactional
    public LikeResponseDto createCommentLike(LoginMemberDto loginMemberDto, Long commentId) {

        Comment comment = commentRepository.getReferenceById(commentId);

        comment.getLikes().stream()
                .filter(like -> like.getMember().getId().equals(loginMemberDto.id()))
                .findAny()
                .ifPresent((like) -> {
                    throw new BusinessException(ErrorCode.ALREADY_LIKED_COMMENT, "already liked comment, member(id) = " + loginMemberDto.id());
                });

        Member member = memberRepository.getReferenceById(loginMemberDto.id());

        Like newLike = Like.builder()
                .comment(comment)
                .member(member)
                .build();

        likeRepository.saveAndFlush(newLike);

        return LikeResponseDto.from(newLike);
    }
}
