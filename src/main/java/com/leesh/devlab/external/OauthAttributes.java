package com.leesh.devlab.external;

import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.domain.member.OauthType;

public interface OauthAttributes {

    String getId();

    OauthType getOauthType();

    default Member toEntity() {
        return Member.createMember(this);
    }

}
