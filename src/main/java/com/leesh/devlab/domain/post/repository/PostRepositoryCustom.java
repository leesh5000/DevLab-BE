package com.leesh.devlab.domain.post.repository;

import com.leesh.devlab.domain.post.Category;
import com.leesh.devlab.dto.PostInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {

    Page<PostInfo> getPostInfoByPaging(Category category, Pageable pageable);

}
