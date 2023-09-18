package com.leesh.devlab.external.implementation.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leesh.devlab.domain.member.constant.OauthType;
import com.leesh.devlab.external.OauthMemberInfo;

public record KakaoMemberInfo() {

    public record Response(String id, @JsonProperty("kakao_account") KakaoAccount kakaoAccount) implements OauthMemberInfo {

        public record KakaoAccount(String email, Profile profile) {

            public record Profile(String nickname,
                                  @JsonProperty("thumbnail_image_url") String thumbnailImageUrl) {

            }
        }

        @Override
        public String getName() {
            return kakaoAccount.profile != null ? kakaoAccount.profile.nickname : null;
        }

        @Override
        public String getEmail() {
            return kakaoAccount.email;
        }

        @Override
        public String getProfileImgUrl() {
            return kakaoAccount.profile != null ? kakaoAccount.profile.thumbnailImageUrl : null;
        }

        @Override
        public OauthType getOauthType() {
            return OauthType.KAKAO;
        }

    }

}
