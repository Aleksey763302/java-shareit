package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Comment;

@Component
public class ItemMapperImpl implements ItemMapper {
    @Override
    public ItemDto itemToItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        return itemDto;
    }

    @Override
    public ItemWithCommentDto itemToItemCommentDto(Item item) {
        ItemWithCommentDto itemWithCommentDto = new ItemWithCommentDto();
        itemWithCommentDto.setId(item.getId());
        itemWithCommentDto.setName(item.getName());
        itemWithCommentDto.setDescription(item.getDescription());
        itemWithCommentDto.setAvailable(item.getAvailable());
        return itemWithCommentDto;
    }

    @Override
    public CommentDto commentToCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getUser().getName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }
}
