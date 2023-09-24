package com.leesh.devlab.validator;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({PARAMETER, FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = Email.EmailValidator.class)
@Documented
public @interface Email {

    String message() default "Invalid email address.";
    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    class EmailValidator implements ConstraintValidator<Email, String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {

            if (value == null) {
                return false;
            }

            Pattern pattern = Pattern.compile("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
            return pattern.matcher(value).matches();
        }
    }

}
