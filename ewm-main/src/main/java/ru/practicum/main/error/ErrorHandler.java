package ru.practicum.main.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.main.error.exception.EntityNotFoundException;
import ru.practicum.main.error.exception.ForbiddenException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    private static final String INVALID_DATA_REASON = "Incorrectly made request";
    private static final String NOT_FOUND_REASON = "The required object was not found";
    private static final String INTEGRITY_CONSTRAINT_REASON = "Integrity constraint has been violated";
    private static final String FORBIDDEN_REASON = "For the requested operation the conditions are not met";
    private static final String INVALID_BODY_REASON = "Invalid request body";
    private static final String BAD_REQUEST_REASON = "Incorrectly made request";

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError incorrectRequestHandle(final ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        StringBuilder errorMessage = new StringBuilder();

        violations.forEach(v -> errorMessage.append("Field: ").append(v.getPropertyPath().toString())
                .append(". Error: ").append(v.getMessage())
                .append(". Value: ").append(v.getInvalidValue()).append("\n"));

        log.warn("Validation error. {}. {}", INVALID_DATA_REASON, errorMessage);

        return new ApiError(
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                INVALID_DATA_REASON,
                errorMessage.toString(),
                getStackTrace(e)
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError invalidArgumentHandle(final MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String errorMessage = fieldErrors.stream()
                .map(error -> String.format("Field: %s. Error: %s. Value: %s",
                        error.getField(), error.getDefaultMessage(), error.getRejectedValue()))
                .collect(Collectors.joining("\n"));

        log.warn("Validation error. {}. {}", INVALID_DATA_REASON, errorMessage);

        return new ApiError(
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                INVALID_DATA_REASON,
                errorMessage,
                getStackTrace(e)
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError notFoundHandle(final EntityNotFoundException e) {
        log.warn("{}. {}", NOT_FOUND_REASON, e.getMessage());
        return new ApiError(
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                NOT_FOUND_REASON,
                e.getMessage(),
                getStackTrace(e)
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError dbErrorHandle(final DataIntegrityViolationException e) {
        log.warn("{}. {}", INTEGRITY_CONSTRAINT_REASON, e.getMessage());
        return new ApiError(
                HttpStatus.CONFLICT.getReasonPhrase(),
                INTEGRITY_CONSTRAINT_REASON,
                e.getMessage(),
                getStackTrace(e)
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError forbiddenHandle(final ForbiddenException e) {
        log.warn("{}. {}", FORBIDDEN_REASON, e.getMessage());
        return new ApiError(
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                FORBIDDEN_REASON,
                e.getMessage(),
                getStackTrace(e)
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError invalidBodyHandle(final HttpMessageNotReadableException e) {
        log.warn("{}. {}", INVALID_BODY_REASON, e.getMessage());
        return new ApiError(
                HttpStatus.CONFLICT.getReasonPhrase(),
                INVALID_BODY_REASON,
                e.getMessage(),
                getStackTrace(e)
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError badRequestHandle(final IllegalStateException e) {
        log.warn("{}. {}", BAD_REQUEST_REASON, e.getMessage());
        return new ApiError(
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                BAD_REQUEST_REASON,
                e.getMessage(),
                getStackTrace(e)
        );
    }

    private List<String> getStackTrace(Throwable e) {
        return Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList());
    }



}
