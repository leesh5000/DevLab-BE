package com.leesh.devlab.service;

import com.leesh.devlab.domain.like.Like;
import com.leesh.devlab.domain.like.LikeRepository;
import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.domain.member.MemberRepository;
import com.leesh.devlab.domain.post.Post;
import com.leesh.devlab.domain.post.repository.PostRepository;
import com.leesh.devlab.dto.LikeInfo;
import com.leesh.devlab.jwt.dto.LoginInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public LikeInfo create(LoginInfo loginInfo, Long postId) {

        Post post = postRepository.getReferenceById(postId);
        Member member = memberRepository.getReferenceById(loginInfo.id());

        Like newLike = Like.builder()
                .post(post)
                .member(member)
                .build();

        likeRepository.saveAndFlush(newLike);

        return LikeInfo.from(newLike);
    }
}
