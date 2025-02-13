package ru.practicum.shareit.util.exceptions;

import lombok.Getter;

@Getter
public class DataAlreadyExistsException extends RuntimeException {
    private final String reason;

    public DataAlreadyExistsException(String reason) {
        super();
        this.reason = reason;
    }
}
