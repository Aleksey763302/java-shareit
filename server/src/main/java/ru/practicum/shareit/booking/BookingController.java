package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.RequestBookingCreate;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService service;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(@RequestBody RequestBookingCreate request,
                                    @RequestHeader(name = USER_ID_HEADER) long bookerId) {
        return service.createBooking(request, bookerId);
    }

    @PatchMapping(path = "/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto approveBooking(@PathVariable long bookingId,
                                     @RequestParam boolean approved,
                                     @RequestHeader(name = USER_ID_HEADER) long userId) {
        return service.approveBooking(bookingId, userId, approved);
    }

    @GetMapping(path = "/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto getBookingByBooker(@PathVariable long bookingId,
                                         @RequestHeader(name = USER_ID_HEADER) long userId) {
        return service.getBookingByBooker(bookingId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getBookingListFromUser(@RequestParam State state,
                                                   @RequestHeader(name = USER_ID_HEADER) long userId) {
        return service.getBookingsByUserId(userId, state);
    }

    @GetMapping(path = "/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getBookingListFromState(@RequestParam State state,
                                                    @RequestHeader(name = USER_ID_HEADER) long userId) {
        return service.getBookingsByOwner(state, userId);
    }
}
