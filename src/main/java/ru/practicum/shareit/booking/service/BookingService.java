package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ApproveBooking;
import ru.practicum.shareit.booking.dto.RequestBookingCreate;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    Optional<BookingDto> createBooking(RequestBookingCreate request);

    Optional<BookingDto> approveBooking(ApproveBooking approveBooking);

    Optional<BookingDto> getBookingByBooker(Long bookingId, Long userId);

    List<BookingDto> getBookingsByOwner(State state, Long userId);

    List<BookingDto> getBookingsByUserId(Long userId, State state);
}
