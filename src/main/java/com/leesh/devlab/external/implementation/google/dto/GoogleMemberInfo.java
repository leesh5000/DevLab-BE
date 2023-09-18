package com.leesh.devlab.external.implementation.google.dto;

import com.leesh.devlab.domain.member.constant.OauthType;
import com.leesh.devlab.external.OauthMemberInfo;

public record GoogleMemberInfo() {

    public record Response(String name, String email, String picture) implements OauthMemberInfo {
        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getEmail() {
            return this.email;
        }

        @Override
        public String getProfileImgUrl() {
            return this.picture;
        }

        @Override
        public OauthType getOauthType() {
            return OauthType.GOOGLE;
        }
    }

}
