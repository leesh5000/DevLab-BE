package com.leesh.devlab.service;

import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.domain.member.MemberRepository;
import com.leesh.devlab.domain.post.repository.PostRepository;
import com.leesh.devlab.dto.*;
import com.leesh.devlab.exception.ErrorCode;
import com.leesh.devlab.exception.custom.AuthException;
import com.leesh.devlab.exception.custom.BusinessException;
import com.leesh.devlab.external.OauthAttributes;
import com.leesh.devlab.jwt.dto.LoginInfo;
import jakarta.servlet.http.HttpSession;
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
    private final PostRepository postRepository;

    public MyProfile getMyProfile(LoginInfo loginInfo) {

        Member member = getById(loginInfo.id());
        Activities activities = getActivities(member);

        return MyProfile.builder()
                .id(member.getId())
                .loginId(member.getLoginId())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .createdAt(member.getCreatedAt())
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
                .id(member.getId())
                .nickname(member.getNickname())
                .createdAt(member.getCreatedAt())
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

        member.updateProfile(updateProfile.nickname(), updateProfile.email());
    }

    @Transactional
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

    @Transactional
    public Member getOrSaveByOauthId(String oauthId, OauthAttributes oauthMember) {
        return memberRepository.findByOauthId(oauthId)
                .orElseGet(() -> {
                    Member entity = oauthMember.toEntity();
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

    public void emailConfirm(LoginInfo loginInfo, EmailConfirm requestDto, HttpSession session) {

        // 세션에 저장된 인증번호를 가져온다.
        String cert = (String) session.getAttribute(requestDto.email());
        if (!requestDto.code().equals(cert)) {
            throw new BusinessException(ErrorCode.WRONG_CERT_NUMBER, "wrong certification number");
        }

        // 인증번호가 일치하면, 토큰 정보를 통해서 유저를 조회한 후 이메일 정보를 업데이트한다.
        Member member = getById(loginInfo.id());
        member.verifyEmail(requestDto.email());

    }
}
