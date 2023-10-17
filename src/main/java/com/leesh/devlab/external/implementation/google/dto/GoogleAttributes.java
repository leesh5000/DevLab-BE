package com.leesh.devlab.external.implementation.google.dto;

import com.leesh.devlab.domain.member.OauthType;
import com.leesh.devlab.external.OauthAttributes;

public record GoogleAttributes(String sub, String email, String picture) implements OauthAttributes {

    @Override
    public String getId() {
        return getOauthType() + "@" + sub;
    }

    @Override
    public OauthType getOauthType() {
        return OauthType.GOOGLE;
    }

}
