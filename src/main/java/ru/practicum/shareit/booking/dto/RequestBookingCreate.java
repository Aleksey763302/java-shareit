package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class RequestBookingCreate {
    private Long bookerId;
    @NotNull
    private Long itemId;
    @NotNull
    private LocalDateTime start;
    @Future
    private LocalDateTime end;
}
