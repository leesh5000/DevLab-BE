package com.leesh.devlab.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;

/**
 * <p>
 *     더욱 객체지향 적인 설계를 위해 의미를 가진 같은 분류의 값들을 묶어만든 불변 클래스
 * </p>
 */
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Embeddable
public class RefreshToken {
    @Column(name = "refresh_token", length = 255, nullable = true)
    private String value;
    @Column(name = "refresh_token_expired_at", nullable = true)
    private Long expiredAt;

    protected RefreshToken(String value, Long expiredAt) {
        this.value = value;
        this.expiredAt = expiredAt;
    }

    public void expiration() {
        this.expiredAt = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return this.expiredAt < System.currentTimeMillis();
    }
}
