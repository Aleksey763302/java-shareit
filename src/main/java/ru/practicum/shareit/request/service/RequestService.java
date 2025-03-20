package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.RequestItemCreate;

import java.util.List;
import java.util.Optional;

public interface RequestService {
    ItemRequestDto createRequest(RequestItemCreate request, long userId);

    Optional<ItemRequestDto> findRequestById(long requestId);

    List<ItemRequestDto> getAllRequests();

    List<ItemRequestDto> getAllRequestsByUserId(long userId);
}
