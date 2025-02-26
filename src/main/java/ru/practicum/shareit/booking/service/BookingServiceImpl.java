package ru.practicum.shareit.booking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.ApproveBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.RequestBookingCreate;
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
    public Optional<BookingDto> createBooking(RequestBookingCreate request) {
        Booking booking = new Booking();
        booking.setStart(request.getStart());
        booking.setEnd(request.getEnd());
        booking.setStatus(Status.WAITING);
        User user = userRepository.findById(request.getBookerId())
                .orElseThrow(() -> new NotFoundUserException("Пользователь не найден"));

        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new NotFoundUserException("Предмет не найден"));
        if (!item.getAvailable()) {
            throw new BookingApprovedException("Предмет не доступен для бронирования");
        }
        booking.setBooker(user);
        booking.setItem(item);
        Booking response = bookingRepository.save(booking);
        BookingDto bookingDto = mapper.bookingToBookingDto(response);
        return Optional.of(bookingDto);
    }

    @Override
    @Transactional
    public Optional<BookingDto> approveBooking(ApproveBooking approveBooking) {
        Booking booking = bookingRepository.findById(approveBooking.getBookingId())
                .orElseThrow(() -> new NotFoundBookingException("Бронь не найдена>"));
        Item item = booking.getItem();
        if (!item.getOwner().getId().equals(approveBooking.getUserId())) {
            throw new AccessDeniedException("Только владелец может одобрить");
        }
        if (!item.getAvailable()) {
            throw new AccessDeniedException("Предмет не доступен для бронирования");
        }
        if (approveBooking.getApprove()) {
            booking.setStatus(Status.APPROVED);
            item.setAvailable(false);
            booking.setItem(item);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        BookingDto response = mapper.bookingToBookingDto(bookingRepository.save(booking));
        return Optional.of(response);
    }

    @Override
    @Transactional
    public Optional<BookingDto> getBookingByBooker(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundBookingException("бронирование не найдено"));
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Доступно только владельцу или автору бронирования.");
        }
        return Optional.of(mapper.bookingToBookingDto(booking));
    }

    @Override
    @Transactional
    public List<BookingDto> getBookingsByOwner(State state, Long userId) {
        Set<Status> status = createStatusList(state);
        List<Booking> bookings = bookingRepository.findByStatusInAndItemOwnerId(status, userId);
        if (bookings.isEmpty()) {
            throw new NotFoundBookingException("бронирование не найдено");
        }
        return bookings
                .stream().map(mapper::bookingToBookingDto)
                .toList();
    }

    @Override
    @Transactional
    public List<BookingDto> getBookingsByUserId(Long userId, State state) {
        Set<Status> status = createStatusList(state);
        LocalDateTime dateTime = LocalDateTime.now();
        List<Booking> response = new ArrayList<>();
        switch (state) {
            case ALL, REJECTED, WAITING -> response = bookingRepository.findByBookerIdAndStatusIn(userId, status);
            case FUTURE ->
                    response = bookingRepository.findByBookerIdAndStatusInAndStartAfter(userId, status, dateTime);
            case PAST -> response = bookingRepository.findByBookerIdAndStatusInAndStartBeforeAndEndBefore(
                    userId, status, dateTime, dateTime);
            case CURRENT -> response = bookingRepository.findByBookerIdAndStatusInAndStartBeforeAndEndAfter(
                    userId, status, dateTime, dateTime);
        }

        return response.stream()
                .sorted(Comparator.comparing(Booking::getStart))
                .map(mapper::bookingToBookingDto)
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
}
