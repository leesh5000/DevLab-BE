package com.leesh.devlab.constant.dto;

import lombok.Builder;

@Builder
public record CommentDetailDto(
        Long id, String contents, String author, Long createdAt, Long modifiedAt,
        Long postId, int likeCount) {

}
