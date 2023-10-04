package com.leesh.devlab.api.post.dto;

import com.leesh.devlab.api.comment.dto.CommentDetail;
import com.leesh.devlab.domain.post.Category;
import lombok.Builder;

import java.util.List;

@Builder
public record PostDetail(
        Long id, String title, String contents, Category category, String author,
        List<CommentDetail> commentDetails,
        List<String> tags,
        int likeCount,
        Long createdAt, Long modifiedAt) {

}
