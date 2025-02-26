package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.RequestCommentCreate;

import java.util.Optional;

public interface CommentService {
    Optional<CommentDto> createComment(RequestCommentCreate request);
}
