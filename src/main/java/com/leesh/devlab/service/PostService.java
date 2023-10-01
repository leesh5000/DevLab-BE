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

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class PostService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final TagService tagService;

    public PostDto.Response create(PostDto.Request requestDto, MemberInfo memberInfo) {

        Member member = memberRepository.getReferenceById(memberInfo.id());

        // 게시글을 생성한다.
        Post newPost = requestDto.toEntity(member);

        // DB에서 태그 목록을 조회한다.
        List<Tag> findTags = tagService.getAllByNames(requestDto.tagNames());

        // 게시글에 해시태그를 추가한다.
        newPost.tagging(findTags);

        postRepository.saveAndFlush(newPost);

        return PostDto.Response.from(newPost.getId());
    }

    public void edit(Long postId, PostDto.Request requestDto, MemberInfo memberInfo) {

        Post findPost = postRepository.findByIdWithHashtags(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_POST, "not found"));

        // 유저가 새로 입력한 태그 목록을 조회한다.
        List<Tag> newTags = tagService.getAllByNames(requestDto.tagNames());

        // 게시글을 수정한다.
        findPost.edit(memberInfo.id(), requestDto.title(), requestDto.contents(), requestDto.category(), newTags);

    }

    public void delete(Long postId, MemberInfo memberInfo) {



    }
}
