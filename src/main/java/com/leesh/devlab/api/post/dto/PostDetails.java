package com.leesh.devlab.api.post.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record PostDetails(
        Long id, String title, String contents, String category, String author,
        List<String> tags,
        int likeCount,
        Long createdAt, Long modifiedAt
) {

}
