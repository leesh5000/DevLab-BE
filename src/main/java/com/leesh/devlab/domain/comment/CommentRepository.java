package com.leesh.devlab.domain.comment;

import com.leesh.devlab.constant.dto.PostCommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

    @Query("select c from Comment c " +
            "inner join fetch c.member " +
            "inner join fetch c.post " +
            "left join fetch c.likes " +
            "where c.id = :id")
    Optional<Comment> findByIdWithEntities(@Param("id") Long id);

    @Query(value = "select c from Comment c " +
            "inner join fetch c.member " +
            "left join fetch c.post ",
            countQuery = "select count(c) from Comment c where c.member.id = :memberId")
    Page<Comment>
    findAllByMemberId(Pageable pageable, Long memberId);

    @Query(value = "select c from Comment c " +
            "inner join fetch c.member m " +
            "where m.id = :memberId",
            countQuery = "select count(c) from Comment c where c.member.id = :memberId")
    Page<Comment> findAllWithMemberByMemberId(@Param("memberId") Long memberId, Pageable pageable);

}
