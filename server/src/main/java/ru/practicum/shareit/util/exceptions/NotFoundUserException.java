package ru.practicum.shareit.util.exceptions;

import lombok.Getter;

@Getter
public class NotFoundUserException extends RuntimeException {
    private final String reason;

    public NotFoundUserException(String reason) {
        super();
        this.reason = reason;
    }
}
