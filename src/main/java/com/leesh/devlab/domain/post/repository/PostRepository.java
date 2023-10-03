package com.leesh.devlab.domain.post.repository;

import com.leesh.devlab.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from Post p " +
            "inner join fetch p.member m " +
            "where p.id = :id")
    Optional<Post> findByIdWithMember(@Param("id") Long id);


    @Query(value = "select p from Post p " +
            "inner join fetch p.member m ",
            countQuery = "select count(p) from Post p")
    Page<Post> findAllWithMember(Pageable pageable);
}
