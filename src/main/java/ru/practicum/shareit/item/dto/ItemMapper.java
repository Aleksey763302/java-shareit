package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Comment;

public interface ItemMapper {
    ItemDto itemToItemDto(Item item);

    ItemWithCommentDto itemToItemCommentDto(Item item);

    CommentDto commentToCommentDto(Comment comment);
}
