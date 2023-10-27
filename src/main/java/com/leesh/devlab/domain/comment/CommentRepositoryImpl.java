package com.leesh.devlab.domain.comment;

import com.leesh.devlab.constant.dto.*;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.leesh.devlab.domain.comment.QComment.comment;
import static com.leesh.devlab.domain.like.QLike.like;
import static com.leesh.devlab.domain.member.QMember.member;
import static com.leesh.devlab.domain.post.QPost.post;
import static com.querydsl.core.types.ExpressionUtils.count;

@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CommentDto> getCommentPage(Pageable pageable, Long memberId) {

        List<CommentDto> content = queryFactory.select(new QCommentDto(
                        comment.id, comment.contents, member.nickname,
                        getCommentLikeCount(),
                        comment.createdAt, comment.modifiedAt,
                        new QCommentDto_PostDto(comment.post.id, comment.post.title, comment.post.category)))
                .from(comment)
                .innerJoin(comment.member, member)
                .leftJoin(comment.post)
                .where(memberIdEq(memberId))
                .orderBy(getOrderBy(pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalCount = queryFactory
                .selectFrom(comment)
                .where(memberIdEq(memberId))
                .fetch().size();

        return new PageImpl<>(content, pageable, totalCount);
    }

    private static Expression<Long> getCommentLikeCount() {
        return ExpressionUtils.as(
                JPAExpressions
                        .select(count(like.id))
                        .from(like)
                        .where(like.comment.eq(comment)),
                "likeCount");
    }

    private static BooleanExpression memberIdEq(Long memberId) {
        return comment.member.id.eq(memberId);
    }

    @Override
    public Page<PostCommentDto> getPostComments(Pageable pageable, Long postId) {

        List<PostCommentDto> content = queryFactory
                .select(new QPostCommentDto(comment.id, comment.contents, member.nickname, getCommentLikeCount(), comment.createdAt, comment.modifiedAt))
                .from(comment)
                .innerJoin(comment.member, member)
                .leftJoin(comment.post, post)
                .where(postIdEq(postId))
                .orderBy(getOrderBy(pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalCount = queryFactory
                .selectFrom(comment)
                .where(postIdEq(postId))
                .fetch().size();

        return new PageImpl<>(content, pageable, totalCount);
    }

    private static BooleanExpression postIdEq(Long postId) {
        return comment.post.id.eq(postId);
    }

    private OrderSpecifier<?>[] getOrderBy(Sort sort) {

        final List<OrderSpecifier<?>> orders = new ArrayList<>();

        for (Sort.Order order : sort) {

            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            switch (order.getProperty()) {
                case "like_count" -> orders.add(new OrderSpecifier<>(direction, comment.likes.size()));
                case "created_at" -> orders.add(new OrderSpecifier<>(direction, comment.createdAt));
                case "modified_at" -> orders.add(new OrderSpecifier<>(direction, comment.modifiedAt));
                default -> {}
            }
        }

        return orders.toArray(OrderSpecifier[]::new);
    }
}
