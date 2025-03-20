package ru.practicum.shareit.booking.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class RequestBookingCreate {
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
