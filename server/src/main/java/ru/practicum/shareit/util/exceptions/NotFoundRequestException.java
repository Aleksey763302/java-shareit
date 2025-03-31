package ru.practicum.shareit.util.exceptions;

import lombok.Getter;

@Getter
public class NotFoundRequestException extends RuntimeException {
    private final String reason;

    public NotFoundRequestException(String reason) {
        super();
        this.reason = reason;
    }
}
