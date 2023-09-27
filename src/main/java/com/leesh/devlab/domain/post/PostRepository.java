package com.leesh.devlab.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from Post p " +
            "inner join fetch p.member m " +
            "left join fetch p.likes " +
            "where m.id = :memberId")
    List<Post> findAllByMemberId(@Param("memberId") Long memberId);

}
