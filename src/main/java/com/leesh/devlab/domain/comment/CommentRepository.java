package com.leesh.devlab.domain.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c " +
            "inner join fetch c.member m " +
            "left join fetch c.likes l " +
            "where m.id = :memberId")
    List<Comment> findAllByMemberIdWithLikes(@Param("memberId") Long memberId);

}
