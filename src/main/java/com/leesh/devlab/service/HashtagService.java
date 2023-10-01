package com.leesh.devlab.service;

import com.leesh.devlab.domain.hashtag.Hashtag;
import com.leesh.devlab.domain.hashtag.HashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class HashtagService {

    private final HashtagRepository hashtagRepository;

    public List<Hashtag> getAllByPostId(Long postId) {

        return hashtagRepository.findAllByPostId(postId);
    }
}
