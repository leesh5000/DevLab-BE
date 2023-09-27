package com.leesh.devlab.api.member.dto;

import lombok.Getter;

@Getter
public class MemberProfile {

    private Long id;
    private String nickname;
    private Long createdAt;
    private Activities activities;

}
