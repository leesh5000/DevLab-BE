package com.leesh.devlab.external.implementation.naver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leesh.devlab.domain.member.OauthType;
import com.leesh.devlab.external.OauthMemberInfo;
import lombok.Getter;

/**
 * <p>
 *      네이버 로그인 API를 통해 받아온 사용자 정보를 담는 DTO
 * </p>
 * <a href={https://developers.naver.com/docs/login/profile/profile.md}>공식문서 링크</a>
 */
@Getter
public class NaverMemberInfo implements OauthMemberInfo {

    private @JsonProperty("resultcode") String resultCode;
    private String message;
    private Response response;

    private NaverMemberInfo() {
    }

    @Getter
    private static class Response {

        private Response() {
        }

        private String id;

    }

    @Override
    public String getOauthId() {
        return getOauthType() + "@" + response.id;
    }

    @Override
    public OauthType getOauthType() {
        return OauthType.NAVER;
    }

}
