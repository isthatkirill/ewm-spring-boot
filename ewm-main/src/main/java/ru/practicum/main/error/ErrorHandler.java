package ru.practicum.main.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.main.error.exception.EntityNotFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    private static final String INVALID_DATA_REASON = "Incorrectly made request";
    private static final String NOT_FOUND_REASON = "The required object was not found.";

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse incorrectRequestHandle(final ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        StringBuilder errorMessage = new StringBuilder();

        for (ConstraintViolation<?> violation : violations) {
            errorMessage.append("Field: ").append(violation.getPropertyPath().toString())
                    .append(". Error: ").append(violation.getMessage())
                    .append(". Value: ").append(violation.getInvalidValue()).append("\n");
        }

        log.warn("Validation error. {}. {}", INVALID_DATA_REASON, errorMessage);

        return new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), INVALID_DATA_REASON, errorMessage.toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse invalidArgumentHandle(final MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String errorMessage = fieldErrors.stream()
                .map(error -> String.format("Field: %s. Error: %s. Value: %s",
                        error.getField(), error.getDefaultMessage(), error.getRejectedValue()))
                .collect(Collectors.joining("\n"));

        log.warn("Validation error. {}. {}", INVALID_DATA_REASON, errorMessage);

        return new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), INVALID_DATA_REASON, errorMessage);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundHandle(final EntityNotFoundException e) {
        log.warn("{}. {}", NOT_FOUND_REASON, e.getMessage());
        return new ErrorResponse(HttpStatus.NOT_FOUND.toString(), NOT_FOUND_REASON, e.getMessage());
    }

}
