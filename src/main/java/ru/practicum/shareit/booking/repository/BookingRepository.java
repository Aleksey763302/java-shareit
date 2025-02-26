package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT DISTINCT b " +
            "FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.id IN :itemId " +
            "AND b.end < :current " +
            "ORDER BY b.end ASC")
    Set<Booking> findLostBooking(Set<Long> itemId, LocalDateTime current);

    default Map<Long, Booking> findLostBookingGroupedByItem(Set<Long> itemId, LocalDateTime current) {
        return findLostBooking(itemId, current).stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), Function.identity()));
    }

    @Query("SELECT DISTINCT b " +
            "FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.id IN :itemId " +
            "AND b.start > :current " +
            "ORDER BY b.end DESC")
    Set<Booking> findNextBooking(Set<Long> itemId, LocalDateTime current);

    default Map<Long, Booking> findNextBookingGroupedByItem(Set<Long> itemId, LocalDateTime current) {
        return findNextBooking(itemId, current).stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), Function.identity()));
    }

    Booking findByBookerIdAndItemId(Long bookerId, Long itemId);

    List<Booking> findByStatusInAndItemOwnerId(Set<Status> statuses, Long ownerId);

    List<Booking> findByBookerIdAndStatusIn(Long id, Set<Status> status);

    List<Booking> findByBookerIdAndStatusInAndStartAfter(Long id,
                                                         Set<Status> status,
                                                         LocalDateTime currentTime);

    List<Booking> findByBookerIdAndStatusInAndStartBeforeAndEndAfter(Long id,
                                                                     Set<Status> status,
                                                                     LocalDateTime start,
                                                                     LocalDateTime end);

    List<Booking> findByBookerIdAndStatusInAndStartBeforeAndEndBefore(Long id,
                                                                      Set<Status> status,
                                                                      LocalDateTime start,
                                                                      LocalDateTime end);
}
