package com.leesh.devlab.service;

import com.leesh.devlab.api.member.dao.MemberDao;
import com.leesh.devlab.api.member.dto.MemberProfile;
import com.leesh.devlab.api.member.dto.MyProfile;
import com.leesh.devlab.api.member.dto.UpdateProfile;
import com.leesh.devlab.constant.ErrorCode;
import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.domain.member.MemberRepository;
import com.leesh.devlab.exception.custom.BusinessException;
import com.leesh.devlab.jwt.dto.MemberInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberDao memberDao;
    private final PasswordEncoder passwordEncoder;

    public MyProfile getMyProfile(MemberInfo memberInfo) {

        MyProfile myProfile = memberDao.getMyProfile(memberInfo.id());

        if (myProfile == null) {
            throw new BusinessException(ErrorCode.NOT_EXIST_MEMBER, "not exist member");
        }

        return myProfile;
    }

    public MemberProfile getProfile(Long memberId) {

        MemberProfile memberProfile = memberDao.getMemberProfile(memberId);

        if (memberProfile == null) {
            throw new BusinessException(ErrorCode.NOT_EXIST_MEMBER, "not exist member");
        }

        return memberProfile;
    }

    public void updateProfile(Long memberId, UpdateProfile updateProfile) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_MEMBER, "not exist member"));

        // 비밀번호가 일치하지 않으면, 예외 발생
        if (!passwordEncoder.matches(updateProfile.password(), member.getPassword())) {
            throw new BusinessException(ErrorCode.WRONG_PASSWORD, "wrong password");
        }

        member.updateProfile(updateProfile.nickname(), updateProfile.email());

    }

    public void deleteMember(Long memberId) {

        memberRepository.deleteById(memberId);

    }
}
