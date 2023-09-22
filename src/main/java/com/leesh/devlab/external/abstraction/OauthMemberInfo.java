package com.leesh.devlab.external.abstraction;

import com.leesh.devlab.constant.OauthType;
import com.leesh.devlab.domain.member.Member;

import java.util.Objects;

/**
 * Oauth Provider로부터 받은 회원 정보를 담는 객체들을 추상화한 인터페이스
 */
public interface OauthMemberInfo {

    String getId();

    OauthType getOauthType();

    /**
     * 멤버 엔티티를 생성하는 메서드
     * @return Member
     */
    default Member toEntity() {
        Objects.requireNonNull(getId(), "OauthMemberInfo id must not be null");
        return Member.of(getOauthType(), getId());
    }

}
