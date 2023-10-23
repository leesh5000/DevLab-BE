package com.leesh.devlab.service;

import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.domain.member.MemberRepository;
import com.leesh.devlab.dto.*;
import com.leesh.devlab.exception.ErrorCode;
import com.leesh.devlab.exception.custom.AuthException;
import com.leesh.devlab.exception.custom.BusinessException;
import com.leesh.devlab.external.OauthAttributes;
import com.leesh.devlab.jwt.dto.LoginInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    public MyProfile getMyProfile(LoginInfo loginInfo) {

        Member member = getById(loginInfo.id());
        Activities activities = getActivities(member);

        return MyProfile.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .createdAt(member.getCreatedAt())
                .securityCode(member.getSecurityCode())
                .introduce(member.getIntroduce())
                .activities(activities)
                .build();
    }

    private Activities getActivities(Member member) {

        int postCount = member.getPosts().size();
        int postLikeCount = member.getPosts().stream()
                .mapToInt(post -> post.getLikes().size())
                .sum();

        int commentCount = member.getComments().size();
        int commentLikeCount = member.getComments().stream()
                .mapToInt(comment -> comment.getLikes().size())
                .sum();

        return Activities.builder()
                .postCount(postCount)
                .postLikeCount(postLikeCount)
                .commentCount(commentCount)
                .commentLikeCount(commentLikeCount)
                .build();
    }

    public MemberProfile getMemberProfile(Long memberId) {

        Member member = getById(memberId);
        Activities activities = getActivities(member);

        return MemberProfile.builder()
                .nickname(member.getNickname())
                .createdAt(member.getCreatedAt())
                .introduce(member.getIntroduce())
                .activities(activities)
                .build();
    }

    @Transactional
    public void updateProfile(Long memberId, UpdateProfile updateProfile) {

        Member member = getById(memberId);

        // 비밀번호가 일치하지 않으면, 예외 발생
        if (!passwordEncoder.matches(updateProfile.password(), member.getPassword())) {
            throw new BusinessException(ErrorCode.WRONG_PASSWORD, "wrong password");
        }

        member.updateProfile(updateProfile.nickname());
    }

    @Transactional
    public void deleteMember(Long memberId) {

        memberRepository.deleteById(memberId);
    }

    private String generateRandom6Digits() {
        return String.valueOf((int) (Math.random() * 899999) + 100000);
    }

    public Member getByRefreshToken(String refreshToken) {
        return memberRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new AuthException(ErrorCode.NOT_EXIST_MEMBER, "not exist member by refresh token = " + refreshToken));
    }

    public Member getById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_MEMBER, "not exist member"));
    }

    @Transactional
    public Member getOrSaveByOauthId(OauthAttributes oauthAttributes) {
        return memberRepository.findByOauthId(oauthAttributes.getId())
                .orElseGet(() -> {
                    Member entity = oauthAttributes.toEntity();
                    return memberRepository.save(entity);
                });
    }

    public Member getByLoginId(Login.Request request) {
        return memberRepository.findByLoginId(request.loginId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_MEMBER, "not exist member"));
    }

    public void existById(Long memberId) throws BusinessException {
        if (!memberRepository.existsById(memberId)) {
            throw new BusinessException(ErrorCode.NOT_EXIST_MEMBER, "not exist member");
        }
    }

    public void checkExistMember(String loginId, String nickname) throws BusinessException {
        if (memberRepository.existsByLoginId(loginId)) {
            throw new BusinessException(ErrorCode.ALREADY_REGISTERED_ID, "already registered id");
        }

        if (memberRepository.existsByNickname(nickname)) {
            throw new BusinessException(ErrorCode.ALREADY_REGISTERED_NICKNAME, "already registered nickname");
        }
    }

    public void checkLoginId(String loginId) {

        if (memberRepository.existsByLoginId(loginId)) {
            throw new BusinessException(ErrorCode.ALREADY_REGISTERED_ID, "already registered id, id = " + loginId);
        }
    }

    public void checkNickname(String nickname) {

        if (memberRepository.existsByNickname(nickname)) {
            throw new BusinessException(ErrorCode.ALREADY_REGISTERED_NICKNAME, "already registered nickname, nickname = " + nickname);
        }
    }

    public void verifyEmail(String email) {



    }
}
