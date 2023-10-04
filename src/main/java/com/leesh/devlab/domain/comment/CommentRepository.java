package com.leesh.devlab.domain.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c " +
            "inner join fetch c.member " +
            "inner join fetch c.post " +
            "left join fetch c.likes " +
            "where c.id = :id")
    Optional<Comment> findByIdWithEntities(@Param("id") Long id);
}
