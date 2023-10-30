package com.leesh.devlab.domain.post;

import com.leesh.devlab.constant.Category;
import com.leesh.devlab.constant.dto.PostDto;
import com.leesh.devlab.constant.dto.QAuthorDto;
import com.leesh.devlab.constant.dto.QPostDto;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.leesh.devlab.domain.comment.QComment.comment;
import static com.leesh.devlab.domain.hashtag.QHashtag.hashtag;
import static com.leesh.devlab.domain.like.QLike.like;
import static com.leesh.devlab.domain.member.QMember.member;
import static com.leesh.devlab.domain.post.QPost.post;
import static com.leesh.devlab.domain.tag.QTag.tag;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.types.ExpressionUtils.count;
import static com.querydsl.core.types.dsl.Expressions.numberTemplate;
import static com.querydsl.core.types.dsl.Expressions.stringTemplate;
import static io.micrometer.common.util.StringUtils.isBlank;

@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PostDto> getPosts(Category category, Pageable pageable, String keyword, Long memberId) {

        List<PostDto> postInfos = queryFactory
                .from(post)
                .innerJoin(post.member, member)
                .leftJoin(post.hashtags, hashtag)
                .leftJoin(hashtag.tag, tag)
                .where(keywordEq(keyword))
                .where(categoryEq(category))
                .where(memberIdEq(memberId))
                .groupBy(post.id)
                .orderBy(getOrderBy(pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .transform(groupBy(post.id).list(
                        new QPostDto(
                                post.id,
                                post.title,
                                post.contents,
                                post.category,
                                new QAuthorDto(member.id, member.nickname),
                                stringTemplate("group_concat({0})", tag.name).as("tags"),
                                ExpressionUtils.as(
                                        JPAExpressions
                                                .select(count(like.id))
                                                .from(like)
                                                .where(like.post.eq(post)),
                                        "likeCount"),
                                ExpressionUtils.as(
                                        JPAExpressions
                                                .select(count(comment.id))
                                                .from(comment)
                                                .where(comment.post.eq(post)),
                                        "commentCount"),
                                post.views,
                                post.createdAt,
                                post.modifiedAt
                        )
                ));

        int totalSize = queryFactory
                .select(post.count())
                .from(post)
                .innerJoin(post.member, member)
                .leftJoin(post.hashtags, hashtag)
                .leftJoin(hashtag.tag, tag)
                .where(keywordEq(keyword))
                .where(categoryEq(category))
                .where(memberIdEq(memberId))
                .groupBy(post.id)
                .fetch()
                .size();

        return new PageImpl<>(postInfos, pageable, totalSize);
    }

    private BooleanExpression memberIdEq(Long memberId) {
        if (memberId != null) {
            return member.id.eq(memberId);
        }
        return null;
    }

    private BooleanExpression keywordEq(String keyword) {

        if (isBlank(keyword)) {
            return null;
        }

        String decodedKeyword = URLDecoder.decode(keyword, StandardCharsets.UTF_8);

        JPQLQuery<Long> tagNameMatchPostIds = JPAExpressions
                .select(post.id)
                .from(post)
                .leftJoin(hashtag).on(hashtag.post.eq(post))
                .leftJoin(tag).on(hashtag.tag.eq(tag))
                .where(numberTemplate(Double.class, "function('match', {0}, {1})", tag.name, "\"" + keyword + "\"").gt(0));

        return numberTemplate(Double.class, "function('matches', {0}, {1}, {2})", post.title, post.contents, "\"" + decodedKeyword + "\"").gt(0)
                .or(post.id.in(tagNameMatchPostIds));
    }

    private static BooleanExpression categoryEq(Category category) {
        return category == null ?  null : post.category.eq(category);
    }

    private OrderSpecifier<?>[] getOrderBy(Sort sort) {

        final List<OrderSpecifier<?>> orders = new ArrayList<>();

        for (Sort.Order order : sort) {

            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            switch (order.getProperty()) {
                case "like_count" -> orders.add(new OrderSpecifier<>(direction, post.likes.size()));
                case "created_at" -> orders.add(new OrderSpecifier<>(direction, post.createdAt));
                case "modified_at" -> orders.add(new OrderSpecifier<>(direction, post.modifiedAt));
                case "view_count" -> orders.add(new OrderSpecifier<>(direction, post.views));
                default -> {}
            }
        }

        return orders.toArray(OrderSpecifier[]::new);
    }
}
