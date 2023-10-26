package com.leesh.devlab.constant.dto;

import com.leesh.devlab.domain.like.Like;

public record LikeResponseDto(Long likeId, Long createdAt, Long modifiedAt) {

    public static LikeResponseDto from(Like like) {
        return new LikeResponseDto(like.getId(), like.getCreatedAt(), like.getModifiedAt());
    }

}
