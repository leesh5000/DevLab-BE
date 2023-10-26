package com.leesh.devlab.constant.dto;

import com.leesh.devlab.domain.comment.Comment;
import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.domain.post.Post;

public record CreateCommentRequestDto(String contents) {

    public Comment toEntity(Post post, Member member) {
        return Comment.builder()
                .contents(contents)
                .post(post)
                .member(member)
                .build();
    }
}
