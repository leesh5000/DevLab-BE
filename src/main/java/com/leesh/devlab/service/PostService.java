package com.leesh.devlab.service;

import com.leesh.devlab.api.post.dto.PostDto;
import com.leesh.devlab.constant.ErrorCode;
import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.domain.member.MemberRepository;
import com.leesh.devlab.domain.post.Post;
import com.leesh.devlab.domain.post.PostRepository;
import com.leesh.devlab.domain.tag.Tag;
import com.leesh.devlab.exception.custom.BusinessException;
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
    private final PostRepository postRepository;
    private final TagService tagService;

    public PostDto.Response create(PostDto.Request requestDto, MemberInfo memberInfo) {

        Member member = memberRepository.getReferenceById(memberInfo.id());

        // 유저가 등록한 태그들을 가져온다.
        Set<Tag> tags = tagService.getAll(requestDto.tagNames());

        // 게시글을 생성한다.
        Post newPost = member.posting(requestDto.title(), requestDto.contents(), requestDto.category(), tags);

        // 응답으로 보낼 ID를 얻기위해 변경감지를 통하지 않고 명시적으로 저장한다.
        postRepository.saveAndFlush(newPost);

        return PostDto.Response.from(newPost.getId());
    }

    public void edit(Long postId, PostDto.Request requestDto, MemberInfo memberInfo) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_POST, "not found"));

        // 유저가 입력한 태그 목록을 조회한다.
        Set<Tag> tags = tagService.getAll(requestDto.tagNames());

        post.edit(memberInfo.id(), requestDto.title(), requestDto.contents(), requestDto.category(), tags);

    }
}
