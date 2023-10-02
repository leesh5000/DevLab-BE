package com.leesh.devlab.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from Post p " +
            "left join fetch p.hashtags h " +
            "left join fetch h.tag t " +
            "inner join fetch p.member m " +
            "where p.id = :id")
    Optional<Post> findByIdWithHashtags(@Param("id") Long id);

}
