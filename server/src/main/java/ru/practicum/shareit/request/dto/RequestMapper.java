package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.ItemRequest;

public interface RequestMapper {
    ItemRequestDto toDto(ItemRequest itemRequest);
}
