package com.leesh.devlab.external.implementation.naver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leesh.devlab.constant.OauthType;
import com.leesh.devlab.external.abstraction.dto.OauthMemberInfo;

public record NaverMemberInfo() {

    public record Response(@JsonProperty("resultcode") String resultCode, String message, @JsonProperty("response") Profile profile) implements OauthMemberInfo {

        public record Profile(String id, String nickname, String email, String name, @JsonProperty("profile_image") String profileImage) {

        }

        @Override
        public String getName() {
            return profile.name;
        }

        @Override
        public String getEmail() {
            return profile.email;
        }

        @Override
        public String getProfileImgUrl() {
            return profile.profileImage;
        }

        @Override
        public OauthType getOauthType() {
            return OauthType.NAVER;
        }
    }
}
