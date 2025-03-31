package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.RequestBookingCreate;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(RequestBookingCreate request, long bookerId);

    BookingDto approveBooking(long bookingId, long userId, boolean approve);

    BookingDto getBookingByBooker(long bookingId, long userId);

    List<BookingDto> getBookingsByOwner(State state, long userId);

    List<BookingDto> getBookingsByUserId(long userId, State state);
}
