package com.leesh.devlab.domain.post;

import com.leesh.devlab.constant.Category;
import com.leesh.devlab.constant.dto.PostDto;
import com.leesh.devlab.constant.dto.PostInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {

    Page<PostDto> getPosts(Category category, Pageable pageable, String keyword, Long memberId);

}
