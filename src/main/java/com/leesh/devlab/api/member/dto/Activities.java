package com.leesh.devlab.api.member.dto;

import lombok.Builder;

@Builder
public record Activities(int postCount, int postLikeCount, int commentCount, int commentLikeCount) {

}