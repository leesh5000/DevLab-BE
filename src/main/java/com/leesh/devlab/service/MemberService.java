package com.leesh.devlab.service;

import com.leesh.devlab.constant.ErrorCode;
import com.leesh.devlab.constant.dto.*;
import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.domain.member.MemberRepository;
import com.leesh.devlab.exception.custom.AuthException;
import com.leesh.devlab.exception.custom.BusinessException;
import com.leesh.devlab.external.OauthAttributes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final MailService mailService;

    public MyProfileResponseDto getMyProfile(LoginMemberDto loginMemberDto) {

        Member member = getById(loginMemberDto.id());
        ActivityDto activities = getActivities(member);

        return MyProfileResponseDto.builder()
                .id(member.getId())
                .loginId(member.getLoginId())
                .nickname(member.getNickname())
                .oauth(member.getOauth())
                .createdAt(member.getCreatedAt())
                .securityCode(member.getSecurityCode())
                .introduce(member.getIntroduce())
                .activities(activities)
                .build();
    }

    private ActivityDto getActivities(Member member) {

        int postCount = member.getPosts().size();
        int postLikeCount = member.getPosts().stream()
                .mapToInt(post -> post.getLikes().size())
                .sum();

        int commentCount = member.getComments().size();
        int commentLikeCount = member.getComments().stream()
                .mapToInt(comment -> comment.getLikes().size())
                .sum();

        return ActivityDto.builder()
                .postCount(postCount)
                .postLikeCount(postLikeCount)
                .commentCount(commentCount)
                .commentLikeCount(commentLikeCount)
                .build();
    }

    public MemberProfileResponseDto getMemberProfile(Long id) {

        Member member = getById(id);
        ActivityDto activities = getActivities(member);

        return MemberProfileResponseDto.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .createdAt(member.getCreatedAt())
                .introduce(member.getIntroduce())
                .activities(activities)
                .build();
    }

    public MemberProfileResponseDto getMemberProfile(String nickname) {

        Member member = getByNickname(nickname);
        ActivityDto activities = getActivities(member);

        return MemberProfileResponseDto.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .createdAt(member.getCreatedAt())
                .introduce(member.getIntroduce())
                .activities(activities)
                .build();
    }

    private Member getByNickname(String nickname) {
        return memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_MEMBER, "not exist member"));
    }

    @Transactional
    public void updateProfile(Long memberId, UpdateProfileRequestDto updateProfileRequestDto) {

        Member member = getById(memberId);

        member.updateProfile(updateProfileRequestDto.nickname(), updateProfileRequestDto.introduce());

        // 이메일 인증된 회원이면, 해당 이메일로 보안코드를 전송한다.
        if (updateProfileRequestDto.email().verified()) {
            String securityCode = member.verify();
            mailService.sendMail(updateProfileRequestDto.email().address(), "[DevLab] 계정 보안코드 안내", "계정 보안코드 : " + securityCode);
        }
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

    public Member getByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId)
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
