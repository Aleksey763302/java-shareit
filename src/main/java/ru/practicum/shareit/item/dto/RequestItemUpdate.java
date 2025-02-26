package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class RequestItemUpdate {
    private Long itemId;
    private Long ownerId;
    private String name;
    private String description;
    private Boolean available;
}
