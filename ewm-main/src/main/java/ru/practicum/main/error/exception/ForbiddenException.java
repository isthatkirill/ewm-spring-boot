package ru.practicum.main.error.exception;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String field, String description, String value) {
        super(String.format("Field: %s. Error: %s. Value: %s", field, description, value));
    }
}
