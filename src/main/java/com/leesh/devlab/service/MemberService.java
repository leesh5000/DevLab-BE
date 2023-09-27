package com.leesh.devlab.service;

import com.leesh.devlab.api.member.dao.MemberDao;
import com.leesh.devlab.api.member.dto.MyProfile;
import com.leesh.devlab.domain.member.MemberRepository;
import com.leesh.devlab.jwt.dto.MemberInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberDao memberDao;

    public MyProfile getMyProfile(MemberInfo memberInfo) {

        return memberDao.getMyProfile(memberInfo.id());
    }

}
