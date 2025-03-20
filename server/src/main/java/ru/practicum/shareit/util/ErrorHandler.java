package ru.practicum.shareit.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import ru.practicum.shareit.util.exceptions.*;

import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> notFoundUser(final NotFoundUserException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getReason()));
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> notFoundItem(final NotFoundItemException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getReason()));
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> notFoundBookingException(final NotFoundBookingException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getReason()));
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> accessDeniedException(final AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getReason()));
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> bookingApprovedException(final BookingApprovedException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> bookingNotAvailableException(final BookingNotAvailableException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> dataAlreadyExistException(final DataAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getReason()));
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> internalServerError(final Throwable e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getLocalizedMessage()));
    }
}
