package com.leesh.devlab.api.member.dto;

import lombok.Getter;

@Getter
public class MyProfile {

    Long id;
    String loginId;
    String nickname;
    String email;
    boolean emailVerified;
    Long createdAt;
    Activity activity;

    @Getter
    public static class Activity {
        int postCount;
        int postLikeCount;
        int commentCount;
        int commentLikeCount;
    }

}
