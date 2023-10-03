package com.leesh.devlab.jwt;

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
