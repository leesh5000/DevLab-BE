package com.leesh.devlab.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.springframework.util.StringUtils;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = Contents.ContentsValidator.class)
@Documented
public @interface Contents {

    String message() default "Invalid email address.";
    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    class ContentsValidator implements ConstraintValidator<Email, String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {

            // 이메일은 빈 값이 들어오면 그냥 통과시킨다.
            if (!StringUtils.hasText(value) || value.length() < 30) {
                return false;
            }

            return true;
        }
    }
}
