package ru.practicum.shareit.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import ru.practicum.shareit.util.exceptions.*;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    public ErrorResponse notFoundUser(final NotFoundUserException e) {
        return ErrorResponse.create(e, HttpStatus.NOT_FOUND, e.getReason());
    }

    @ExceptionHandler
    public ErrorResponse notFoundItem(final NotFoundItemException e) {
        return ErrorResponse.create(e, HttpStatus.NOT_FOUND, e.getReason());
    }

    @ExceptionHandler
    public ErrorResponse validationErrorParam(final NotValidParamException e) {
        return ErrorResponse.create(e, HttpStatus.BAD_REQUEST, e.getReason());
    }

    @ExceptionHandler
    public ErrorResponse accessDeniedException(final AccessDeniedException e) {
        return ErrorResponse.create(e, HttpStatus.FORBIDDEN, e.getReason());
    }

    @ExceptionHandler
    public ErrorResponse dataAlreadyExistException(final DataAlreadyExistsException e) {
        return ErrorResponse.create(e, HttpStatus.CONFLICT, e.getReason());
    }

    @ExceptionHandler
    public ErrorResponse internalServerError(final Throwable e) {
        return ErrorResponse.create(e, HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }
}
