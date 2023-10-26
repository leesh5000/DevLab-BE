package com.leesh.devlab.domain.comment;

import com.leesh.devlab.constant.dto.CommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepositoryCustom {

    Page<CommentDto> getCommentPage(Pageable pageable, Long memberId);

}
