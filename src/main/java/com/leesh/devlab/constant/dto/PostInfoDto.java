package com.leesh.devlab.constant.dto;

import com.leesh.devlab.constant.Category;
import com.querydsl.core.annotations.QueryProjection;

import java.util.Arrays;
import java.util.List;

public record PostInfoDto(Long id, String title, String contents, Category category, Long createdAt, Long modifiedAt,
                       String author, long commentCount, long likeCount, List<String> tags) {

    @QueryProjection
    public PostInfoDto(Long id, String title, String contents, Category category, Long createdAt, Long modifiedAt, String author, long commentCount, long likeCount, String tags) {
        this(id, title, contents, category, createdAt, modifiedAt, author, commentCount, likeCount,
                tags != null ? Arrays.stream(tags.split(",")).toList() : List.of());
    }
}
