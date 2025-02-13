package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.util.exceptions.NotFoundItemException;
import ru.practicum.shareit.util.exceptions.AccessDeniedException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final List<Item> items;
    private Long maxId = 1L;

    @Override
    public Optional<ItemDto> createItem(ItemDto item, Long userId) {
        Item newItem = new Item();
        newItem.setId(maxId);
        maxId++;
        newItem.setOwner(userId);
        newItem.setName(item.getName());
        newItem.setDescription(item.getDescription());
        newItem.setAvailable(item.getAvailable());
        items.add(newItem);
        return Optional.of(ItemMapper.mapToItemDto(newItem));
    }

    @Override
    public Optional<ItemDto> updateItem(ItemDto itemUpdate, Long userId, Long itemId) {
        Optional<Item> itemOptional = items.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst();
        if (itemOptional.isEmpty()) {
            throw new NotFoundItemException("Item not found for update");
        }
        Item saveItem = itemOptional.get();
        if(!saveItem.getOwner().equals(userId)){
            throw new AccessDeniedException("Editing is only available to the owner");
        }
        items.remove(saveItem);
        if (Objects.nonNull(itemUpdate.getName())) {
            saveItem.setName(itemUpdate.getName());
        }
        if (Objects.nonNull(itemUpdate.getDescription())) {
            saveItem.setDescription(itemUpdate.getDescription());
        }
        if (Objects.nonNull(itemUpdate.getAvailable())) {
            saveItem.setAvailable(itemUpdate.getAvailable());
        }
        items.add(saveItem);
        return Optional.of(ItemMapper.mapToItemDto(saveItem));
    }

    @Override
    public Optional<ItemDto> getItemById(Long userId, Long itemId) {
        return items.stream()
                .filter(item -> item.getOwner().equals(userId) && item.getId().equals(itemId))
                .map(ItemMapper::mapToItemDto).findFirst();
    }

    @Override
    public Optional<List<ItemDto>> getItemsByUserId(Long userId) {
        List<ItemDto> response = items.stream()
                .filter(item -> item.getOwner().equals(userId))
                .map(ItemMapper::mapToItemDto).toList();
        return Optional.of(response);
    }

    @Override
    public Optional<List<ItemDto>> searchItems(String text) {
        if (text.isBlank()) {
            return Optional.of(List.of());
        }
        List<ItemDto> response = items.stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) && item.getAvailable())
                .map(ItemMapper::mapToItemDto).toList();
        return Optional.of(response);
    }

    @Override
    public void deleteItemById(Long userId, Long itemId) {
        Optional<Item> itemOptional = getItemFromDb(userId, itemId);
        if (itemOptional.isEmpty()) {
            throw new NotFoundItemException("Item not found for deletion");
        }
        items.remove(itemOptional.get());
    }

    private Optional<Item> getItemFromDb(Long userId, Long itemId) {
        return items.stream()
                .filter(item -> item.getOwner().equals(userId) && item.getId().equals(itemId))
                .findFirst();
    }
}
