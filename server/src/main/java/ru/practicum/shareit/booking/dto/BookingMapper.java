package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookingDto toDto(Booking booking);
}
