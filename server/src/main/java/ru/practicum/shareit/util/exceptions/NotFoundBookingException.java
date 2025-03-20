package ru.practicum.shareit.util.exceptions;

import lombok.Getter;

@Getter
public class NotFoundBookingException extends RuntimeException {
    private final String reason;

    public NotFoundBookingException(String reason) {
        super();
        this.reason = reason;
    }
}
