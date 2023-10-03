package com.leesh.devlab.service;

import com.leesh.devlab.domain.comment.Comment;
import com.leesh.devlab.domain.comment.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public List<Comment> getByMemberIdWithLikes(Long memberId) {
        return commentRepository.findAllByMemberIdWithLikes(memberId);
    }
}
