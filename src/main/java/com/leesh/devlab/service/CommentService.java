package com.leesh.devlab.service;

import com.leesh.devlab.domain.comment.Comment;
import com.leesh.devlab.domain.comment.CommentRepository;
import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.domain.member.MemberRepository;
import com.leesh.devlab.domain.post.Post;
import com.leesh.devlab.domain.post.PostRepository;
import com.leesh.devlab.constant.dto.CommentDetailDto;
import com.leesh.devlab.constant.dto.CommentDto;
import com.leesh.devlab.constant.dto.CreateCommentRequestDto;
import com.leesh.devlab.constant.dto.CreateCommentResponseDto;
import com.leesh.devlab.constant.ErrorCode;
import com.leesh.devlab.exception.custom.BusinessException;
import com.leesh.devlab.constant.dto.LoginMemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final PostRepository postRepository;

    @Transactional
    public CreateCommentResponseDto create(CreateCommentRequestDto requestDto, LoginMemberDto loginMemberDto, Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_RESOURCE, "not found post with id : " + postId));

        Member member = memberRepository.getReferenceById(loginMemberDto.id());

        Comment comment = requestDto.toEntity(post, member);

        Long id = commentRepository.save(comment).getId();

        return new CreateCommentResponseDto(id);
    }

    @Transactional
    public void put(CreateCommentRequestDto requestDto, LoginMemberDto loginMemberDto, Long postId, Long commentId) {

        Optional<Comment> optional = commentRepository.findById(commentId);

        if (optional.isPresent()) {
            Comment comment = optional.get();
            comment.edit(requestDto.contents(), loginMemberDto);
        } else {
            this.create(requestDto, loginMemberDto, postId);
        }
    }

    private Comment getByIdWithEntities(Long commentId) {
        return commentRepository.findByIdWithEntities(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_RESOURCE, "not found comment with id : " + commentId));
    }

    public CommentDetailDto generateCommentDetail(Comment comment) {

            return CommentDetailDto.builder()
                    .id(comment.getId())
                    .contents(comment.getContents())
                    .author(comment.getMember().getNickname())
                    .postId(comment.getPost().getId())
                    .likeCount(comment.getLikes().size())
                    .createdAt(comment.getCreatedAt())
                    .modifiedAt(comment.getModifiedAt())
                    .build();
    }

    @Transactional
    public void delete(Long commentId, LoginMemberDto loginMemberDto) {

        Comment comment = getByIdWithEntities(commentId);

        validateAuthor(loginMemberDto, comment);

        commentRepository.delete(comment);
    }

    private void validateAuthor(LoginMemberDto loginMemberDto, Comment comment) {
        if (!Objects.equals(comment.getMember().getId(), loginMemberDto.id())) {
            throw new BusinessException(ErrorCode.NOT_RESOURCE_OWNER, "login user is not comment's author, login user id = " + loginMemberDto.id() + ", comment's author id = " + comment.getMember().getId());
        }
    }

    public CommentDetailDto getDetails(Long commentId) {

        Comment comment = getByIdWithEntities(commentId);

        return generateCommentDetail(comment);
    }

    public Page<CommentDetailDto> getLists(Pageable pageable) {

        Page<Comment> comments = commentRepository.findAllByMemberId(pageable, null);

        return comments.map(this::generateCommentDetail);

    }

    public Page<CommentDto> getLists(Pageable pageable, Long memberId) {

        Page<CommentDto> comments = commentRepository.getCommentPage(pageable, memberId);

        return comments;

    }

    public Page<CommentDetailDto> getListsByMemberId(Long memberId, Pageable pageable) {

        memberService.existById(memberId);

        Page<Comment> page = commentRepository.findAllWithMemberByMemberId(memberId, pageable);

        return page.map(this::generateCommentDetail);
    }
}
