package ru.practicum.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse missingHeaderHandle(final ConstraintViolationException e) {
        log.warn("{}: {}", e.getClass().getSimpleName(), e.getMessage());
        return new ErrorResponse("Validation error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse missingHeaderHandle(final MethodArgumentNotValidException e) {
        log.warn("{}: {}", e.getClass().getSimpleName(), e.getMessage());
        return new ErrorResponse("Validation error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse missingRequestParameterHandle(final MissingServletRequestParameterException e) {
        log.warn("{}: {}", e.getClass().getSimpleName(), e.getMessage());
        return new ErrorResponse("Missing request parameter", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse illegalStateHandle(final IllegalStateException e) {
        log.warn("{}: {}", e.getClass().getSimpleName(), e.getMessage());
        return new ErrorResponse("Invalid arguments given", e.getMessage());
    }

}
