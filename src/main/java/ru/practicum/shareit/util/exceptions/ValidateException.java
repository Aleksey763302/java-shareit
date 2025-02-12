package ru.practicum.shareit.util.exceptions;

import lombok.Getter;

@Getter
public class ValidateException extends RuntimeException {
    private final String reason;

    public ValidateException(String reason) {
        super();
        this.reason = reason;
    }
}
