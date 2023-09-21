package com.leesh.devlab.constant;

import lombok.Getter;

@Getter
public enum GrantType {

    BEARER("Bearer");

    GrantType(String type) {
        this.type = type;
    }

    private final String type;

    public static boolean isBearerType(String type) {
        return GrantType.BEARER.type.equals(type);
    }

}
