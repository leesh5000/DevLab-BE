package com.leesh.devlab.service;

import com.leesh.devlab.domain.comment.Comment;
import com.leesh.devlab.domain.comment.CommentRepository;
import com.leesh.devlab.domain.like.Like;
import com.leesh.devlab.domain.like.LikeRepository;
import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.domain.member.MemberRepository;
import com.leesh.devlab.domain.post.Post;
import com.leesh.devlab.domain.post.repository.PostRepository;
import com.leesh.devlab.dto.CommentDetail;
import com.leesh.devlab.dto.CreateComment;
import com.leesh.devlab.exception.ErrorCode;
import com.leesh.devlab.exception.custom.BusinessException;
import com.leesh.devlab.jwt.dto.LoginInfo;
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
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final LikeRepository likeRepository;
    private final MemberService memberService;
    private final PostRepository postRepository;

    @Transactional
    public CreateComment.Response create(CreateComment.Request requestDto, LoginInfo loginInfo, Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_RESOURCE, "not found post with id : " + postId));

        Member member = memberRepository.getReferenceById(loginInfo.id());

        Comment comment = requestDto.toEntity(post, member);

        Long id = commentRepository.save(comment).getId();

        return new CreateComment.Response(id);
    }

    @Transactional
    public void put(CreateComment.Request requestDto, LoginInfo loginInfo, Long postId, Long commentId) {

        Optional<Comment> optional = commentRepository.findById(commentId);

        if (optional.isPresent()) {
            Comment comment = optional.get();
            comment.edit(requestDto.contents());
        } else {
            this.create(requestDto, loginInfo, postId);
        }
    }

    private Comment getByIdWithEntities(Long commentId) {
        return commentRepository.findByIdWithEntities(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_RESOURCE, "not found comment with id : " + commentId));
    }

    public CommentDetail generateCommentDetail(Comment comment) {

            return CommentDetail.builder()
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
    public void delete(Long commentId, LoginInfo loginInfo) {

        Comment comment = getByIdWithEntities(commentId);

        validateAuthor(loginInfo, comment);

        deleteAllChildren(comment);

        commentRepository.delete(comment);
    }

    private void deleteAllChildren(Comment comment) {

        List<Long> likeIds = comment.getLikes()
                .stream()
                .map(Like::getId)
                .toList();

        likeRepository.deleteAllByIdInBatch(likeIds);
    }

    private void validateAuthor(LoginInfo loginInfo, Comment comment) {
        if (!Objects.equals(comment.getMember().getId(), loginInfo.id())) {
            throw new BusinessException(ErrorCode.NOT_RESOURCE_OWNER, "login user is not comment's author, login user id = " + loginInfo.id() + ", comment's author id = " + comment.getMember().getId());
        }
    }

    public CommentDetail getDetails(Long commentId) {

        Comment comment = getByIdWithEntities(commentId);

        return generateCommentDetail(comment);
    }

    public Page<CommentDetail> getLists(Pageable pageable) {

        Page<Comment> comments = commentRepository.findAllWithMemberAndPost(pageable);

        return comments.map(this::generateCommentDetail);

    }

    public Page<CommentDetail> getListsByMemberId(Long memberId, Pageable pageable) {

        memberService.existById(memberId);

        Page<Comment> page = commentRepository.findAllWithMemberByMemberId(memberId, pageable);

        return page.map(this::generateCommentDetail);
    }
}
