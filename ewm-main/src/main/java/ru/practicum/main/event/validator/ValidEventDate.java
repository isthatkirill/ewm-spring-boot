package ru.practicum.main.event.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EventDateValidator.class)
public @interface ValidEventDate {
    String message() default "EventDate must be at least 2 hours later than the current time and cannot be null";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}