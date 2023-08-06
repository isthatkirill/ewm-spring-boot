package ru.practicum.main.error;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ru.practicum.stats.util.Formats.DATE_PATTERN;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ErrorResponse {

    String status;
    String reason;
    String message;
    final String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_PATTERN));

}