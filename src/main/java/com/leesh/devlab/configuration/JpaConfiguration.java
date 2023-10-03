package com.leesh.devlab.configuration;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

/**
 * <p>
 *  Base Entity의 createdBy, modifiedBy 필드에 이벤트로 값을 넣어주는 설정<br>
 *  영속성 컨텍스트의 관리 하에 저장되어야 값이 Audit 가능
 * </p>
 * {@link com.leesh.devlab.domain.BaseEntity#createdBy}
 * {@link com.leesh.devlab.domain.BaseEntity#modifiedBy}
 *
 * 어떻게 생성된 엔티티인지 알기 위해 HTTP URI를 넣어준다.
 */
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@EnableJpaAuditing
@Configuration
public class JpaConfiguration {

    @Bean
    public AuditorAware<String> auditorAware() {

        return () -> {

            String modifiedBy = "unknown";

            if (RequestContextHolder.getRequestAttributes() != null) {

                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                        .getRequestAttributes())
                        .getRequest();

                // 생성자, 수정자에 Reuqest URI를 넣어준다.
                if (StringUtils.hasText(request.getRequestURI())) {
                    modifiedBy = request.getRequestURI();
                }
            }

            return Optional.of(modifiedBy);
        };
    }

}
