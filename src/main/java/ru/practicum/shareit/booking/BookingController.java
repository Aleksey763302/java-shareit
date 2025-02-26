package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ApproveBooking;
import ru.practicum.shareit.booking.dto.RequestBookingCreate;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService service;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Optional<BookingDto> createBooking(@Valid @RequestBody RequestBookingCreate request,
                                              @RequestHeader(name = USER_ID_HEADER) Long userId) {
        request.setBookerId(userId);
        return service.createBooking(request);
    }

    @PatchMapping(path = "/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<BookingDto> approveBooking(@PathVariable Long bookingId,
                                               @RequestParam Boolean approved,
                                               @RequestHeader(name = USER_ID_HEADER) Long userId) {
        ApproveBooking approveBooking = ApproveBooking.builder()
                .approve(approved).bookingId(bookingId).userId(userId).build();
        return service.approveBooking(approveBooking);
    }

    @GetMapping(path = "/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<BookingDto> getBookingByBooker(@PathVariable Long bookingId,
                                                   @RequestHeader(name = USER_ID_HEADER) Long userId) {
        return service.getBookingByBooker(bookingId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getBookingListFromUser(@RequestParam(required = false) State state,
                                                   @RequestHeader(name = USER_ID_HEADER) Long userId) {
        if (Objects.isNull(state)) {
            state = State.ALL;
        }
        return service.getBookingsByUserId(userId, state);
    }

    @GetMapping(path = "/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getBookingListFromState(@RequestParam(required = false) State state,
                                                    @RequestHeader(name = USER_ID_HEADER) Long userId) {
        if (Objects.isNull(state)) {
            state = State.ALL;
        }
        return service.getBookingsByOwner(state, userId);
    }
}
