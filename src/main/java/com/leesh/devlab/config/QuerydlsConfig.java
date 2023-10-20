package com.leesh.devlab.config;

import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class QuerydlsConfig {

    private final EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {

        // Hibernate 6.x에서 HibernateHandler 대신 DefaultQueryHandler를 사용하는 현상 때문에 결과 집합 쿼리 Transfrom이 에러가 나는 현상이 있음
        // 따라서, JPAQueryFactory를 직접 생성해서 사용해야함
        // https://github.com/querydsl/querydsl/issues/3428 참고
        return new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
    }

}
