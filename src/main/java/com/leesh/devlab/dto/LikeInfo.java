package com.leesh.devlab.dto;

import com.leesh.devlab.domain.like.Like;

public record LikeInfo(Long likeId, Long createdAt, Long modifiedAt) {

    public static LikeInfo from(Like like) {
        return new LikeInfo(like.getId(), like.getCreatedAt(), like.getModifiedAt());
    }

}
