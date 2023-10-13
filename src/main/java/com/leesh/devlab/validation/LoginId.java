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
@Constraint(validatedBy = LoginId.LoginIdValidator.class)
@Documented
public @interface LoginId {

    String message() default "Nickname must be at least 2 characters and not more than 20 characters.";
    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    class LoginIdValidator implements ConstraintValidator<LoginId, String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {

            if (!StringUtils.hasText(value)) {
                return false;
            }

            // 영어, 숫자로 된 4~20자리
            Pattern pattern = Pattern.compile("^[a-z0-9]{4,20}$");
            return pattern.matcher(value).matches();
        }
    }

}
