package com.leesh.devlab.domain.comment;

import com.leesh.devlab.constant.dto.CommentDto;
import com.leesh.devlab.constant.dto.QCommentDto;
import com.leesh.devlab.constant.dto.QCommentDto_PostDto;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
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
import static com.querydsl.core.types.ExpressionUtils.count;

@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CommentDto> getCommentPage(Pageable pageable, Long memberId) {

        List<CommentDto> commentDtos = queryFactory.select(new QCommentDto(
                        comment.id, comment.contents, member.nickname,
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(count(like.id))
                                        .from(like)
                                        .where(like.comment.eq(comment)),
                                "likeCount"),
                        comment.createdAt, comment.modifiedAt,
                        new QCommentDto_PostDto(comment.post.id, comment.post.title, comment.post.category)))
                .from(comment)
                .innerJoin(comment.member, member)
                .leftJoin(comment.post)
                .where(comment.member.id.eq(memberId))
                .orderBy(getOrderBy(pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalCount = queryFactory
                .selectFrom(comment)
                .where(comment.member.id.eq(memberId))
                .fetch().size();

        return new PageImpl<>(commentDtos, pageable, totalCount);
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
