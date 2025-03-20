package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestMapperImpl implements RequestMapper {
    private final ItemMapper itemMapper;

    @Override
    public ItemRequestDto toDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(itemRequest.getCreated());
        List<Item> items = itemRequest.getItems();
        if (Objects.nonNull(items)) {
            List<ItemDto> itemsDto = items
                    .stream()
                    .map(itemMapper::itemToItemDto)
                    .toList();
            itemRequestDto.setItems(itemsDto);
        }
        return itemRequestDto;
    }
}
