package com.leesh.devlab.domain.post.repository;

import com.leesh.devlab.domain.post.Category;
import com.leesh.devlab.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    @Query("select p from Post p " +
            "inner join fetch p.member m " +
            "where p.id = :id")
    Optional<Post> findByIdWithMember(@Param("id") Long id);


    @Query(value = "select p from Post p " +
            "inner join fetch p.member m " +
            "where p.category = :category",
            countQuery = "select count(p) from Post p where p.category = :category")
    Page<Post> findAllByCategory(@Param("category") Category category, Pageable pageable);

    @Query(value = "select p from Post p " +
            "inner join fetch p.member m " +
            "where m.id = :memberId",
            countQuery = "select count(p) from Post p where p.member.id = :memberId")
    Page<Post> findAllWithMemberByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    @Query(nativeQuery = true,
            value = "select p.* from post p " +
                    "inner join member m on p.member_id = m.id " +
                    "where m.id = :memberId",
            countQuery = "select count(p) from Post p where p.member.id = :memberId")
    Page<Post> findAllByPage(Pageable pageable);

}
