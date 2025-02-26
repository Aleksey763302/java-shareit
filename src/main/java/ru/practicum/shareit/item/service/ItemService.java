package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentDto;
import ru.practicum.shareit.item.dto.RequestItemCreate;
import ru.practicum.shareit.item.dto.RequestItemUpdate;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    Optional<ItemDto> createItem(RequestItemCreate request);

    Optional<ItemDto> updateItem(RequestItemUpdate item);

    Optional<ItemWithCommentDto> getItemById(Long userId, Long itemId);

    Optional<List<ItemWithCommentDto>> getItemsByUserId(Long userId);

    Optional<List<ItemDto>> searchItems(String text);

    void deleteItemById(Long userId, Long itemId);
}
