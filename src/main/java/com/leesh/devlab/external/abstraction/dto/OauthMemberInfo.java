package com.leesh.devlab.external.abstraction.dto;

import com.leesh.devlab.constant.OauthType;

public interface OauthMemberInfo {

    String getName();

    String getEmail();

    String getProfileImgUrl();

    OauthType getOauthType();
}
