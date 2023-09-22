package com.leesh.devlab.external.implementation.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leesh.devlab.constant.OauthType;
import com.leesh.devlab.external.abstraction.OauthMemberInfo;
import lombok.Getter;

public record KakaoMemberInfo(String id, @JsonProperty("kakao_account") KakaoAccount kakaoAccount) implements OauthMemberInfo {

    @Getter
    public static class KakaoAccount {

        private String email;
        private Profile profile;

        @Getter
        public static class Profile {

            private String nickname;
            private @JsonProperty("thumbnail_image_url") String thumbnailImageUrl;

        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public OauthType getOauthType() {
        return OauthType.KAKAO;
    }

}
