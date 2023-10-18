package com.leesh.devlab.external;

import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.domain.member.OauthType;

import java.util.UUID;

public interface OauthAttributes {

    String getId();

    OauthType getOauthType();

    default String getNickname() {
        return getOauthType().toString().charAt(0) + UUID.randomUUID().toString().split("-")[0];
    };

    default Member toEntity() {
        return Member.of(getOauthType(), getId(), getNickname());
    }

}
