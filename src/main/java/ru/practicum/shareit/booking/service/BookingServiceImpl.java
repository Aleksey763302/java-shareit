package ru.practicum.shareit.booking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.RequestBookingCreate;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.exceptions.AccessDeniedException;
import ru.practicum.shareit.util.exceptions.BookingApprovedException;
import ru.practicum.shareit.util.exceptions.NotFoundBookingException;
import ru.practicum.shareit.util.exceptions.NotFoundUserException;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper mapper;

    @Override
    @Transactional
    public BookingDto createBooking(RequestBookingCreate request, long bookerId) {
        Booking booking = new Booking();
        booking.setStart(request.getStart());
        booking.setEnd(request.getEnd());
        booking.setStatus(Status.WAITING);
        User user = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundUserException("Пользователь не найден"));

        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new NotFoundUserException("Предмет не найден"));
        if (!item.getAvailable()) {
            throw new BookingApprovedException("Предмет не доступен для бронирования");
        }
        booking.setBooker(user);
        booking.setItem(item);
        Booking response = bookingRepository.save(booking);
        return mapper.toDto(response);
    }

    @Override
    @Transactional
    public BookingDto approveBooking(long bookingId, long userId, boolean approve) {
        Booking booking = findBookingById(bookingId);
        Item item = booking.getItem();
        if (!item.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Только владелец может одобрить");
        }
        if (!item.getAvailable()) {
            throw new AccessDeniedException("Предмет не доступен для бронирования");
        }
        if (approve) {
            booking.setStatus(Status.APPROVED);
            item.setAvailable(false);
            booking.setItem(item);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return mapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingByBooker(long bookingId, long userId) {
        Booking booking = findBookingById(bookingId);
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Доступно только владельцу или автору бронирования.");
        }
        return mapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsByOwner(State state, long userId) {
        Set<Status> status = createStatusList(state);
        List<Booking> bookings = bookingRepository.findByStatusInAndItemOwnerId(status, userId);
        if (bookings.isEmpty()) {
            throw new NotFoundBookingException("бронирование не найдено");
        }
        return bookings
                .stream().map(mapper::toDto)
                .toList();
    }

    @Override
    public List<BookingDto> getBookingsByUserId(long userId, State state) {
        Set<Status> status = createStatusList(state);
        LocalDateTime dateTime = LocalDateTime.now();
        List<Booking> response = new ArrayList<>();
        switch (state) {
            case ALL, REJECTED, WAITING -> response = bookingRepository.findByBookerIdAndStatusIn(userId, status);
            case FUTURE ->
                    response = bookingRepository.findByBookerIdAndStatusInAndStartAfterOrderByStartAsc(userId, status, dateTime);
            case PAST -> response = bookingRepository.findByBookerIdAndStatusInAndStartBeforeAndEndBefore(
                    userId, status, dateTime, dateTime);
            case CURRENT ->
                    response = bookingRepository.findByBookerIdAndStatusInAndStartBeforeAndEndAfterOrderByStartAsc(
                            userId, status, dateTime, dateTime);
        }

        return response.stream()
                .map(mapper::toDto)
                .toList();
    }

    private Set<Status> createStatusList(State state) {
        Set<Status> status = new HashSet<>();
        if (state == State.CURRENT) {
            status.add(Status.APPROVED);
        }
        if (state == State.FUTURE || state == State.PAST) {
            status.add(Status.WAITING);
            status.add(Status.APPROVED);
        }
        if (state == State.ALL) {
            status.add(Status.APPROVED);
            status.add(Status.WAITING);
            status.add(Status.REJECTED);
        }
        if (state == State.WAITING) {
            status.add(Status.WAITING);
        }
        if (state == State.REJECTED) {
            status.add(Status.REJECTED);
        }
        return status;
    }

    private Booking findBookingById(long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundBookingException("бронирование не найдено"));
    }
}
