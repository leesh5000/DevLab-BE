package com.leesh.devlab.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.springframework.util.StringUtils;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = Nickname.NicknameValidator.class)
@Documented
public @interface Nickname {

    String message() default "Nickname must be at least 2 characters and not more than 10 characters.";
    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    class NicknameValidator implements ConstraintValidator<Nickname, String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {

            if (!StringUtils.hasText(value)) {
                return false;
            }

            // 한글, 영어, 숫자로 된 2~10자리
            Pattern pattern = Pattern.compile("^[a-zA-Z0-9가-힣]{2,20}$");
            return pattern.matcher(value).matches();
        }
    }
}
