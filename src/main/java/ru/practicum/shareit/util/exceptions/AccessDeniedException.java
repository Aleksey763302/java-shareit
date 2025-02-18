package ru.practicum.shareit.util.exceptions;

import lombok.Getter;

@Getter
public class AccessDeniedException extends RuntimeException {
    private final String reason;

    public AccessDeniedException(String reason) {
        super();
        this.reason = reason;
    }
}
