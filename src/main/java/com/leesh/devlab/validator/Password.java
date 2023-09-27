package com.leesh.devlab.validator;

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
@Constraint(validatedBy = Password.PasswordValidator.class)
@Documented
public @interface Password {

    String message() default "Password must be at least 4 characters and not more than 30 characters.";
    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    class PasswordValidator implements ConstraintValidator<Password, String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {

            if (!StringUtils.hasText(value)) {
                return false;
            }

            Pattern pattern = Pattern.compile("^.{4,30}$");
            return pattern.matcher(value).matches();
        }
    }

}
