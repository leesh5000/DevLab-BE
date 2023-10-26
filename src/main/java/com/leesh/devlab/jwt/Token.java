package com.leesh.devlab.jwt;

import com.leesh.devlab.constant.TokenType;

public interface Token {

    TokenType getTokenType();

    String getValue();

    int getExpiresInSeconds();

}
