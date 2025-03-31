package ru.practicum.shareit.util.exceptions;

import lombok.Getter;

@Getter
public class EmptyStringToSearchException extends RuntimeException {
    private final String reason;

    public EmptyStringToSearchException(String reason) {
        super();
        this.reason = reason;
    }
}
