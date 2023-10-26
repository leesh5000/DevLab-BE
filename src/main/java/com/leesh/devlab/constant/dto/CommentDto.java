package com.leesh.devlab.constant.dto;

import com.leesh.devlab.constant.Category;
import com.querydsl.core.annotations.QueryProjection;

public record CommentDto(Long id, String contents, String author, long likeCount, Long createdAt, Long modifiedAt, PostDto post) {

    @QueryProjection
    public CommentDto {

    }

    public record PostDto(Long id, String title, Category category) {

        @QueryProjection
        public PostDto {

        }
    }
}
