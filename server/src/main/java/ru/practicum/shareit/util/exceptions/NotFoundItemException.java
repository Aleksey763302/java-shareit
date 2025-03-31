package ru.practicum.shareit.util.exceptions;

import lombok.Getter;

@Getter
public class NotFoundItemException extends RuntimeException {
    private final String reason;

    public NotFoundItemException(String reason) {
        super();
        this.reason = reason;
    }
}
