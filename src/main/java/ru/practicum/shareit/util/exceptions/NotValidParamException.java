package ru.practicum.shareit.util.exceptions;

import lombok.Getter;

@Getter
public class NotValidParamException extends RuntimeException {
    private final String reason;

    public NotValidParamException(String reason) {
        super();
        this.reason = reason;
    }
}
