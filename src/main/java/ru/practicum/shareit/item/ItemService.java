package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    Optional<ItemDto> createItem(ItemDto item, Long userId);

    Optional<ItemDto> updateItem(ItemDto item, Long userId, Long itemId);

    Optional<ItemDto> getItemById(Long userId, Long itemId);

    Optional<List<ItemDto>> getItemsByUserId(Long userId);

    Optional<List<ItemDto>> searchItems(String text);

    void deleteItemById(Long userId, Long itemId);
}
