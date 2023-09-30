package com.leesh.devlab.validator;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Set;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = Tags.TagsValidator.class)
@Documented
public @interface Tags {

    String message() default "Tag's size cannot exceed 10.";
    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    class TagsValidator implements ConstraintValidator<Tags, Set<String>> {

        @Override
        public boolean isValid(Set<String> value, ConstraintValidatorContext context) {

            return value.size() <= 10;
        }
    }

}
