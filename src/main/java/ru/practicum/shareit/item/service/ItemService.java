package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentDto;
import ru.practicum.shareit.item.model.RequestItemCreate;
import ru.practicum.shareit.item.model.RequestItemUpdate;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    ItemDto createItem(RequestItemCreate request, long ownerId);

    ItemDto updateItem(RequestItemUpdate item, long itemId, long ownerId);

    Optional<ItemWithCommentDto> getItemById(long userId, long itemId);

    List<ItemWithCommentDto> getItemsByUserId(long userId);

    List<ItemDto> searchItems(String text);

    void deleteItemById(long userId, long itemId);
}
