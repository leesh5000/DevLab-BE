package com.leesh.devlab.service;

import com.leesh.devlab.domain.tag.Tag;
import com.leesh.devlab.domain.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class TagService {

    private final TagRepository tagRepository;

    /**
     * <p>
     *     DB에서 태그 목록들을 조회한다. <br>
     *     DB에 존재하지 않는 태그는 저장하고 조회함으로써 항상 입력값으로 들어온 태그 목록들이 존재함을 보장한다.
     * </p>
     * @param tagNames
     * @return
     */
    public List<Tag> getAllByNames(Set<String> tagNames) {

        // 태그 이름을 소문자로 변환한다.
        tagNames = tagNames.stream().map(String::toLowerCase).collect(Collectors.toSet());

        // 유저가 입력한 태그 목록을 조회한다.
        List<Tag> findTags = tagRepository.findAllByName(tagNames);

        // 이전에 저장되지 않은 태그를 찾는다. (tagNames에는 DB에서 찾지 못한 태그 이름만 남는다.)
        Set<String> findTagNames = findTags.stream().map(Tag::getName).collect(Collectors.toSet());
        tagNames.removeAll(findTagNames);

        // 이전에 저장되지 않은 태그를 저장하고, findTags에 추가한다.
        List<Tag> newTags = tagNames.stream()
                .map(Tag::new)
                .toList();

        tagRepository.saveAll(newTags);
        findTags.addAll(newTags);

        return findTags;
    }

}
