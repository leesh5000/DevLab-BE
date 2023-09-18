package com.leesh.devlab.external;

import com.leesh.devlab.domain.member.constant.OauthType;

public interface OauthMemberInfo {

    String getName();

    String getEmail();

    String getProfileImgUrl();

    OauthType getOauthType();
}
