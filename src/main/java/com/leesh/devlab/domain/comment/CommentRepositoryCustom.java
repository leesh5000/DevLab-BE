package com.leesh.devlab.domain.comment;

import com.leesh.devlab.constant.dto.CommentDto;
import com.leesh.devlab.constant.dto.PostCommentDto;
import com.leesh.devlab.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepositoryCustom {

    Page<CommentDto> getCommentPage(Pageable pageable, Long memberId);

    Page<PostCommentDto> getPostComments(Pageable pageable, Long postId);

}
