package ru.practicum.shareit.util;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import ru.practicum.shareit.util.exceptions.*;

import java.util.Objects;

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
    public ErrorResponse notFoundBookingException(final NotFoundBookingException e) {
        return ErrorResponse.create(e, HttpStatus.NOT_FOUND, e.getReason());
    }

    @ExceptionHandler
    public ErrorResponse validationErrorParam(final MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(e.getLocalizedMessage());
        return ErrorResponse.create(e, HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler
    public ErrorResponse emptyStringToSearchException(final EmptyStringToSearchException e) {
        return ErrorResponse.create(e, HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler
    public ErrorResponse missingRequestHeaderException(final MissingRequestHeaderException e) {
        return ErrorResponse.create(e, HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler
    public ErrorResponse accessDeniedException(final AccessDeniedException e) {
        return ErrorResponse.create(e, HttpStatus.FORBIDDEN, e.getReason());
    }

    @ExceptionHandler
    public ErrorResponse bookingApprovedException(final BookingApprovedException e) {
        return ErrorResponse.create(e, HttpStatus.BAD_REQUEST, e.getMessage());
    }


    @ExceptionHandler
    public ErrorResponse dataAlreadyExistException(final DataAlreadyExistsException e) {
        return ErrorResponse.create(e, HttpStatus.CONFLICT, e.getReason());
    }

    @ExceptionHandler
    public ErrorResponse internalServerError(final Throwable e) {
        return ErrorResponse.create(e, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
}
