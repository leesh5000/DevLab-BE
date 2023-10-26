package com.leesh.devlab.constant.dto;

import lombok.Builder;

@Builder
public record ActivityDto(int postCount, int postLikeCount, int commentCount, int commentLikeCount) {

}