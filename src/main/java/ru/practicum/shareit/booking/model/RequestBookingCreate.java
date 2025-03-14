package ru.practicum.shareit.booking.model;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class RequestBookingCreate {
    @NotNull
    private Long itemId;
    @NotNull
    private LocalDateTime start;
    @Future
    private LocalDateTime end;
}
