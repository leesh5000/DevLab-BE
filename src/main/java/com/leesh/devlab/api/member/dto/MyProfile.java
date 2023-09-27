package com.leesh.devlab.api.member.dto;

import lombok.Getter;

@Getter
public class MyProfile {

    private Long id;
    private String loginId;
    private String nickname;
    private String email;
    private boolean emailVerified;
    private Long createdAt;
    private Activities activities;

}
