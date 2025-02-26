package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class RequestCommentCreate {
    private Long itemId;
    private Long userId;
    private String text;
}
