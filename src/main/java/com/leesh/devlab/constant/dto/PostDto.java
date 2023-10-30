package com.leesh.devlab.constant.dto;

import com.leesh.devlab.constant.Category;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;

import java.util.Arrays;
import java.util.List;

@Builder
public record PostDto(
        Long id, String title, String contents, Category category, AuthorDto author,
        List<String> tags,
        long likeCount, long commentCount, long viewCount,
        Long createdAt, Long modifiedAt) {

    @QueryProjection
    public PostDto(Long id, String title, String contents, Category category, AuthorDto author, String tags, long likeCount, long commentCount, long viewCount, Long createdAt, Long modifiedAt) {
        this(id, title, contents, category, author,
                tags != null ? Arrays.stream(tags.split(",")).toList() : List.of(),
                likeCount, commentCount, viewCount, createdAt, modifiedAt);
    }
}
