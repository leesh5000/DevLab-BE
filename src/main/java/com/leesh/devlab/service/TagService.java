package com.leesh.devlab.service;

import com.leesh.devlab.domain.tag.Tag;
import com.leesh.devlab.domain.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class TagService {

    private final TagRepository tagRepository;

    public Set<Tag> getAll(Set<String> tagNames) {

        // 태그 이름을 소문자로 변환한다.
        tagNames = tagNames.stream().map(String::toLowerCase).collect(Collectors.toSet());

        // 유저가 입력한 태그 목록을 조회한다.
        Set<Tag> tags = tagRepository.findAllByName(tagNames);

        // 처음 등록하는 태그라면, 새로운 엔티티를 생성한 뒤 tags에 추가한다.
        tagNames.forEach(tagName -> {
            Tag tag = Tag.from(tagName);
            tags.add(tag);
        });
        return tags;
    }

}
