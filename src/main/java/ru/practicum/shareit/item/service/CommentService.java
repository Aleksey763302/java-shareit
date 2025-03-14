package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.RequestCommentCreate;

public interface CommentService {
    CommentDto createComment(RequestCommentCreate body, long userId, long itemId);
}
