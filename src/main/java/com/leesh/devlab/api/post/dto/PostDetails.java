package com.leesh.devlab.api.post.dto;

import com.leesh.devlab.domain.post.Category;
import lombok.Builder;

import java.util.List;

@Builder
public record PostDetails(
        Long id, String title, String contents, Category category, String author,
        List<String> tags,
        int likeCount,
        Long createdAt, Long modifiedAt) {

    public PostDetails {
    }

}
