package ru.practicum.main.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = DateValidator.class)
public @interface EventDateValidator {
    String message() default "The date and time on which the event is scheduled cannot be" +
            " earlier than two hours from the current moment";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}