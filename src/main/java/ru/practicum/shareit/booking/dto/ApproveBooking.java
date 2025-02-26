package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ApproveBooking {
    Long bookingId;
    Boolean approve;
    Long userId;
}
