package com.leesh.devlab.domain.member;

import com.leesh.devlab.constant.OauthType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.NoArgsConstructor;


@Embeddable
@NoArgsConstructor
public class Oauth {

    protected Oauth(OauthType type, String id) {
        this.type = type;
        this.id = id;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth_type", nullable = true)
    private OauthType type;

    @Column(name = "oauth_id", nullable = true, length = 255, unique = true)
    private String id;

}
