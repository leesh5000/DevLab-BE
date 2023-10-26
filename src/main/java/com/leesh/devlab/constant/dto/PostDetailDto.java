package com.leesh.devlab.constant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leesh.devlab.constant.Category;
import lombok.Builder;

import java.util.List;

@Builder
public record PostDetailDto(
        Long id, String title, String contents, Category category, String author,
        @JsonProperty("comment_details") List<CommentDetailDto> commentDetailDtos,
        List<String> tags,
        int likeCount,
        Long createdAt, Long modifiedAt) {

}
