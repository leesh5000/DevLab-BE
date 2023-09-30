package com.leesh.devlab.service;

import com.leesh.devlab.api.post.dto.PostDto;
import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.domain.member.MemberRepository;
import com.leesh.devlab.domain.post.Post;
import com.leesh.devlab.domain.tag.Tag;
import com.leesh.devlab.domain.tag.TagRepository;
import com.leesh.devlab.jwt.dto.MemberInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@RequiredArgsConstructor
@Transactional
@Service
public class PostService {

    private final MemberRepository memberRepository;
    private final TagRepository tagRepository;
    private final TagService tagService;

    public PostDto.Response createPost(PostDto.Request requestDto, MemberInfo memberInfo) {

        Member member = memberRepository.getReferenceById(memberInfo.id());

        // 게시글 엔티티를 생성한다.
        Post post = member.posting(requestDto.title(), requestDto.contents(), requestDto.category());

        // 유저가 입력한 태그 목록을 조회한다.
        Set<Tag> tags = tagService.getAll(requestDto.tags());

        // 게시글에 태깅한다.
        for (Tag tag : tags) {
            post.tagging(tag);
        }

        return PostDto.Response.from(post);
    }
}
