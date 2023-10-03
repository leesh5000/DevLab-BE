package com.leesh.devlab.service;

import com.leesh.devlab.domain.hashtag.Hashtag;
import com.leesh.devlab.domain.hashtag.HashtagRepository;
import com.leesh.devlab.domain.post.Post;
import com.leesh.devlab.domain.tag.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class HashtagService {

    private final HashtagRepository hashtagRepository;

    public List<Hashtag> create(Post newPost, List<Tag> findTags) {

        // 해시태그 데이터를 생성 후 DB에 저장한다.
        List<Hashtag> hashtags = findTags.stream()
                .map(tag -> Hashtag.builder()
                        .post(newPost)
                        .tag(tag)
                        .build())
                .toList();

        hashtagRepository.saveAll(hashtags);

        return hashtags;

    }
}
