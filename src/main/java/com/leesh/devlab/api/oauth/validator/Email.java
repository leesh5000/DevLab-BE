package com.leesh.devlab.api.oauth.validator;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = Email.EmailValidator.class)
@Documented
public @interface Email {

    String message() default "invalid email.";
    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    class EmailValidator implements ConstraintValidator<Email, String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            Pattern pattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
            return pattern.matcher(value).matches();
        }
    }

}
