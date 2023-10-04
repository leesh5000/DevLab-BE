package com.leesh.devlab.dto;

import lombok.Builder;

@Builder
public record CommentDetail(
        Long id, String contents, String author, Long createdAt, Long modifiedAt,
        Long postId, int likeCount) {

}
