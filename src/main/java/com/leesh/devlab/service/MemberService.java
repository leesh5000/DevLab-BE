package com.leesh.devlab.service;

import com.leesh.devlab.api.auth.dto.LoginInfo;
import com.leesh.devlab.api.member.dao.MemberDao;
import com.leesh.devlab.api.member.dto.*;
import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.domain.member.MemberRepository;
import com.leesh.devlab.exception.ErrorCode;
import com.leesh.devlab.exception.custom.AuthException;
import com.leesh.devlab.exception.custom.BusinessException;
import com.leesh.devlab.external.OauthMemberInfo;
import com.leesh.devlab.jwt.dto.MemberInfo;
import jakarta.servlet.http.HttpSession;
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
    private final MailService mailService;

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

        Member member = getById(memberId);

        // 비밀번호가 일치하지 않으면, 예외 발생
        if (!passwordEncoder.matches(updateProfile.password(), member.getPassword())) {
            throw new BusinessException(ErrorCode.WRONG_PASSWORD, "wrong password");
        }

        member.updateProfile(updateProfile.nickname(), updateProfile.email());

    }

    public void deleteMember(Long memberId) {

        memberRepository.deleteById(memberId);

    }

    private String generateRandom6Digits() {
        return String.valueOf((int) (Math.random() * 899999) + 100000);
    }

    public void emailVerify(EmailVerify requestDto, HttpSession session) {

        String randomNumber = generateRandom6Digits();

        // TODO : 추후 스케일 아웃이 고려될 때 Redis와 같은 외부 저장소를 사용하여 인증 번호를 관리할 것
        session.setAttribute(requestDto.email(), randomNumber);

        // 이메일 인증을 진행한다.
        String title = "[DevLab] 이메일 인증번호 안내";
        String contents = "[이메일 인증번호] " + randomNumber + "\n" +
                "인증번호 유효시간은 3분 입니다.";

        mailService.sendMail(requestDto.email(), title, contents);


    }


    public Member getByRefreshToken(String refreshToken) {
        return memberRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new AuthException(ErrorCode.INVALID_TOKEN, "not invalid refresh token"));
    }

    public Member getById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_MEMBER, "not exist member"));
    }

    public Member getByEmail(String email) throws BusinessException {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_MEMBER, "not exist member"));
    }

    public Member getOrSaveByOauthId(String oauthId, OauthMemberInfo oauthMember) {
        return memberRepository.findByOauthId(oauthId)
                .orElseGet(() -> {
                    Member entity = oauthMember.toEntity();
                    return memberRepository.save(entity);
                });
    }

    public Member getByLoginId(LoginInfo.Request request) {
        return memberRepository.findByLoginId(request.loginId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_MEMBER, "not exist member"));
    }

    public void checkExistMember(String loginId, String nickname) throws BusinessException {
        if (memberRepository.existsByLoginId(loginId)) {
            throw new BusinessException(ErrorCode.ALREADY_REGISTERED_ID, "already registered id");
        }

        if (memberRepository.existsByNickname(nickname)) {
            throw new BusinessException(ErrorCode.ALREADY_REGISTERED_NICKNAME, "already registered nickname");
        }
    }

    public void emailConfirm(MemberInfo memberInfo, EmailConfirm requestDto, HttpSession session) {

        // 세션에 저장된 인증번호를 가져온다.
        String cert = (String) session.getAttribute(requestDto.email());
        if (!requestDto.code().equals(cert)) {
            throw new BusinessException(ErrorCode.WRONG_CERT_NUMBER, "wrong certification number");
        }

        // 인증번호가 일치하면, 토큰 정보를 통해서 유저를 조회한 후 이메일 정보를 업데이트한다.
        Member member = getById(memberInfo.id());
        member.verifyEmail(requestDto.email());

    }
}
