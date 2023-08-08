package ru.practicum.main.event.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class EventDateValidator implements ConstraintValidator<ValidEventDate, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime eventDate, ConstraintValidatorContext context) {
        if (eventDate == null) {
            return false;
        }

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime minValidEventDate = currentTime.plusHours(2);

        return eventDate.isAfter(minValidEventDate);
    }
}

