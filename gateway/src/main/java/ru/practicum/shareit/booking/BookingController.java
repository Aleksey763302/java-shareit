package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.RequestBookingCreate;
import ru.practicum.shareit.booking.dto.State;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createBooking(@Valid @RequestBody RequestBookingCreate request,
                                                @RequestHeader(name = USER_ID_HEADER) long bookerId) {
        return bookingClient.bookItem(bookerId, request);
    }

    @PatchMapping(path = "/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> approveBooking(@PathVariable long bookingId,
                                                 @RequestParam boolean approved,
                                                 @RequestHeader(name = USER_ID_HEADER) long userId) {
        return bookingClient.approveBooking(bookingId, userId, approved);
    }

    @GetMapping(path = "/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getBookingByBooker(@PathVariable long bookingId,
                                                     @RequestHeader(name = USER_ID_HEADER) long userId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getBookingListFromUser(@RequestParam(defaultValue = "ALL") String state,
                                                         @RequestParam(defaultValue = "0") int from,
                                                         @RequestParam(defaultValue = "10") int size,
                                                         @RequestHeader(name = USER_ID_HEADER) long userId) {
        return bookingClient.getBookings(userId, State.from(state).orElseThrow(), from, size);
    }

    @GetMapping(path = "/owner")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getBookingListFromState(@RequestParam(defaultValue = "ALL") State state,
                                                          @RequestHeader(name = USER_ID_HEADER) long userId) {
        return bookingClient.getBookingsByOwner(state, userId);
    }
}
